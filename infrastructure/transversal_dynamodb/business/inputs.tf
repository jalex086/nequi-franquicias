# Variables de entrada para el módulo transversal DynamoDB

variable "env" {
  description = "Environment (dev, qa, prod)"
  type        = string
  validation {
    condition     = contains(["dev", "qa", "prod"], var.env)
    error_message = "Environment must be dev, qa, or prod."
  }
}

variable "capacity" {
  description = "Business capacity identifier"
  type        = string
  default     = "business"
}

variable "owner" {
  description = "Team owner of the resources"
  type        = string
  default     = "platform-team"
}

variable "tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default     = {}
}
