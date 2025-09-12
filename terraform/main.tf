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

provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args        = ["eks", "get-token", "--cluster-name", var.cluster_name, "--region", var.aws_region]
  }
}

data "aws_vpc" "existing" {
  id = var.vpc_id
}

# Reference existing private subnets
data "aws_subnets" "private" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.existing.id]
  }

  # Filter for your private subnets
  filter {
    name   = "tag:Type"
    values = ["Private"]
  }
}

# Reference existing public subnets
data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.existing.id]
  }

  # Filter for your public subnets
  filter {
    name   = "tag:Type"
    values = ["Public"]
  }
}

data "aws_subnet" "selected_public" {
  for_each = toset(data.aws_subnets.public.ids)
  id       = each.value
}

data "aws_subnet" "selected_private" {
  for_each = toset(data.aws_subnets.private.ids)
  id       = each.value
}

resource "aws_security_group" "eks" {
  name        = "${var.project_name}-eks-sg"
  description = "Security group for EKS cluster"
  vpc_id      = data.aws_vpc.existing.id

  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-eks-sg"
    Project     = var.project_name
    Environment = var.environment
  }
}

resource "aws_security_group_rule" "eks_to_rds" {
  type                     = "egress"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"
  security_group_id        = aws_security_group.eks.id
  source_security_group_id = var.rds_security_group_id
  description              = "Allow EKS pods to communicate with RDS"
}

resource "aws_security_group_rule" "rds_from_eks" {
  type                     = "ingress"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"
  security_group_id        = var.rds_security_group_id
  source_security_group_id = aws_security_group.eks.id
  description              = "Allow RDS to accept connections from EKS"
}



module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 19.0"

  cluster_name    = var.cluster_name
  cluster_version = var.cluster_version

  vpc_id     = var.vpc_id
  subnet_ids = data.aws_subnets.private.ids

  # Reduce cluster logging to save resources
  cluster_enabled_log_types = ["api", "audit"]

  # Optimize node group for t3.micro
  eks_managed_node_groups = {
    main = {
      name           = "main"
      instance_types = var.node_instance_types
      capacity_type  = "ON_DEMAND"
      desired_size   = var.node_desired_capacity
      max_size       = var.node_max_capacity
      min_size       = var.node_min_capacity

      # Critical for small instances - reduce resource requirements
      ami_type  = "AL2_x86_64"
      disk_size = 20

      # Optimize kubelet for small instances
      bootstrap_extra_args = "--kubelet-extra-args '--max-pods=10 --kube-reserved memory=0.1Gi,cpu=100m --eviction-hard memory.available<200Mi'"

      # Use launch template for additional configuration
      use_custom_launch_template = true

      # Apply tags
      tags = {
        Environment = var.environment
        Project     = var.project_name
      }
    }
  }

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}