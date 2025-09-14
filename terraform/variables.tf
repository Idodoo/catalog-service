variable "aws_region" {
    description = "AWS region"
    type        = string
    default     = "eu-north-1"
  }

  variable "environment" {
    description = "Environment (dev, staging, prod)"
    type        = string
    default     = "dev"
  }

  variable "project_name" {
    description = "Name of the project"
    type        = string
    default     = "catalog-service"
  }

  variable "cluster_name" {
    description = "Name of the EKS cluster"
    type        = string
    default     = "eks-catalog-service"
  }

  variable "cluster_version" {
    description = "Kubernetes version"
    type        = string
    default     = "1.29"
  }

  variable "node_instance_types" {
    description = "Instance types for the EKS nodes"
    type        = list(string)
    default     = ["t3.small"]
  }

  variable "node_desired_capacity" {
    description = "Desired number of nodes"
    type        = number
    default     = 1
  }

  variable "node_max_capacity" {
    description = "Maximum number of nodes"
    type        = number
    default     = 2
  }

  variable "node_min_capacity" {
    description = "Minimum number of nodes"
    type        = number
    default     = 1
  }

  variable "rds_security_group_id" {
    description = "Security group ID for RDS"
    type        = string
    default     = "sg-05f9a494137d70ccc"
  }

  variable "rds_vpc_id" {
    description = "VPC ID where RDS is located"
    type        = string
    default     = "vpc-07e55a7f817da6bbb"
  }

  variable "rds_vpc_cidr" {
    description = "CIDR block of the VPC where RDS is located"
    type        = string
    default     = "10.0.0.0/16"
  }