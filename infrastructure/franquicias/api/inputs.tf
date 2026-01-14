# Variables de entrada para el módulo específico de franquicias

variable "container_image" {
  description = "Docker image for the application"
  type        = string
  default     = "jalex86/nequi-franquicias"
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "env" {
  description = "Environment (dev, qa, pdn)"
  type        = string
  validation {
    condition     = contains(["dev", "qa", "pdn"], var.env)
    error_message = "Environment must be dev, qa, or pdn."
  }
}

variable "capacity" {
  description = "Business capacity identifier"
  type        = string
  default     = "business"
}

variable "functionality" {
  description = "Specific functionality name"
  type        = string
  default     = "franquicias"
}

variable "owner" {
  description = "Team owner of the resources"
  type        = string
  default     = "platform-team"
}

variable "project" {
  description = "Project identifier"
  type        = string
  default     = "franchise-api"
}

variable "tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default     = {}
}
