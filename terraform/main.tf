terraform {
                required_version = ">= 1.0.0"
                required_providers {
                  aws = {
                    source  = "hashicorp/aws"
                    version = ">= 5.0"
                  }
                  kubernetes = {
                    source  = "hashicorp/kubernetes"
                    version = "~> 2.23"
                  }
                }

                backend "s3" {
                  bucket         = "webapp-sever-bbd15058b8979cc5"
                  key            = "eks/terraform.tfstate"
                  region         = "eu-north-1"
                  dynamodb_table = "terraform-state-lock"
                  encrypt        = true
                }
              }

              provider "aws" {
                region = var.aws_region
              }

              locals {
                cluster_name = var.cluster_name
                tags = {
                  Environment = var.environment
                  Project     = var.project_name
                  ManagedBy   = "Terraform"
                }
              }

              # Create a new VPC specifically for EKS
              module "vpc" {
                source  = "terraform-aws-modules/vpc/aws"
                version = "~> 5.0"

                name = "${var.project_name}-vpc"
                cidr = "10.1.0.0/16"  # Different CIDR from existing VPC to avoid conflicts

                azs             = ["${var.aws_region}a", "${var.aws_region}b"]
                private_subnets = ["10.1.1.0/24", "10.1.2.0/24"]
                public_subnets  = ["10.1.101.0/24", "10.1.102.0/24"]

                # NAT Gateway for private subnet internet access (crucial for EKS node bootstrapping)
                enable_nat_gateway     = true
                single_nat_gateway     = true
                one_nat_gateway_per_az = false
                enable_dns_hostnames   = true
                enable_dns_support     = true

                # EKS-specific subnet tagging built-in
                public_subnet_tags = {
                  "kubernetes.io/cluster/${local.cluster_name}" = "shared"
                  "kubernetes.io/role/elb"                      = "1"
                }

                private_subnet_tags = {
                  "kubernetes.io/cluster/${local.cluster_name}" = "shared"
                  "kubernetes.io/role/internal-elb"             = "1"
                }

                tags = local.tags
              }

              # Create a security group for EKS
              resource "aws_security_group" "eks" {
                name        = "${var.project_name}-eks-sg"
                description = "Security group for EKS cluster"
                vpc_id      = module.vpc.vpc_id

                egress {
                  from_port   = 0
                  to_port     = 0
                  protocol    = "-1"
                  cidr_blocks = ["0.0.0.0/0"]
                }

                tags = merge(
                  {
                    Name = "${var.project_name}-eks-sg"
                  },
                  local.tags
                )
              }

              # VPC peering to connect with RDS VPC (assuming RDS is in a different VPC)
              resource "aws_vpc_peering_connection" "eks_to_rds" {
                vpc_id      = module.vpc.vpc_id
                peer_vpc_id = var.rds_vpc_id
                auto_accept = true

                tags = {
                  Name = "${var.project_name}-eks-to-rds-peering"
                }
              }

              # Add routes for VPC peering
              resource "aws_route" "eks_to_rds" {
                count                     = length(module.vpc.private_route_table_ids)
                route_table_id            = module.vpc.private_route_table_ids[count.index]
                destination_cidr_block    = var.rds_vpc_cidr
                vpc_peering_connection_id = aws_vpc_peering_connection.eks_to_rds.id
              }

              # Create the EKS cluster
              module "eks" {
                source  = "terraform-aws-modules/eks/aws"
                version = "~> 19.0"

                cluster_name    = var.cluster_name
                cluster_version = var.cluster_version

                vpc_id                         = module.vpc.vpc_id
                subnet_ids                     = module.vpc.private_subnets
                control_plane_subnet_ids       = module.vpc.private_subnets
                cluster_endpoint_public_access = true

                # Enable OIDC provider for service accounts
                enable_irsa = true

                # EKS Add-ons
                cluster_addons = {
                  coredns = {
                    most_recent = true
                  }
                  kube-proxy = {
                    most_recent = true
                  }
                  vpc-cni = {
                    most_recent = true
                  }
                }

                eks_managed_node_group_defaults = {
                  ami_type       = "AL2_x86_64"
                  disk_size      = 30
                  instance_types = var.node_instance_types
                }

                eks_managed_node_groups = {
                  main = {
                    name = "main"

                    min_size     = var.node_min_capacity
                    max_size     = var.node_max_capacity
                    desired_size = var.node_desired_capacity

                    instance_types = var.node_instance_types
                    capacity_type  = "ON_DEMAND"

                    # No custom launch template needed
                    use_custom_launch_template = false

                    # IAM policies
                    iam_role_additional_policies = {
                      AmazonEKSWorkerNodePolicy          = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
                      AmazonEC2ContainerRegistryReadOnly = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
                      AmazonEKS_CNI_Policy               = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
                      AmazonSSMManagedInstanceCore       = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
                    }
                  }
                }

                tags = local.tags
              }

              # Create security group rule to allow EKS to communicate with RDS
              resource "aws_security_group_rule" "eks_to_rds" {
                type                     = "egress"
                from_port                = 5432
                to_port                  = 5432
                protocol                 = "tcp"
                security_group_id        = module.eks.node_security_group_id
                source_security_group_id = var.rds_security_group_id
                description              = "Allow EKS pods to communicate with RDS"
              }

              resource "aws_security_group_rule" "rds_from_eks" {
                type                     = "ingress"
                from_port                = 5432
                to_port                  = 5432
                protocol                 = "tcp"
                security_group_id        = var.rds_security_group_id
                source_security_group_id = module.eks.node_security_group_id
                description              = "Allow RDS to accept connections from EKS"
              }

              provider "kubernetes" {
                host                   = module.eks.cluster_endpoint
                cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
                exec {
                  api_version = "client.authentication.k8s.io/v1beta1"
                  command     = "aws"
                  args        = ["eks", "get-token", "--cluster-name", var.cluster_name, "--region", var.aws_region]
                }
              }