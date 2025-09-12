variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-north-1"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "eks-catalog-service"
}

variable "environment" {
  description = "Environment"
  type        = string
  default     = "dev"
}

variable "rds_security_group_id" {
  description = "Security group ID of the RDS instance"
  type        = string
}

variable "vpc_id" {
  description = "ID of the existing VPC"
  type        = string
}

variable "cluster_name" {
  description = "Name of the EKS cluster"
  type        = string
  default     = "eks-cluster"
}

variable "cluster_version" {
  description = "Kubernetes version for EKS cluster"
  type        = string
  default     = "1.29"
}

variable "node_instance_types" {
  description = "EC2 instance types for  EKS node groups"
  type        = list(string)
  default     = ["t3.micro"]
  validation {
    condition     = contains(["t3.micro", "t3.small"], var.node_instance_types[0])
    error_message = "For free tier, only t3.micro and t3.small are allowed."
  }
}

variable "node_desired_capacity" {
  description = "Desired number of nodes"
  type        = number
  default     = 1
}

variable "node_max_capacity" {
  description = "Maximum number of nodes"
  type        = number
  default     = 1
}

variable "node_min_capacity" {
  description = "Minimum number of nodes"
  type        = number
  default     = 1
}

