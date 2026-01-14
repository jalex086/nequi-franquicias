# Variables de entrada para el módulo transversal DynamoDB

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
  description = "Functionality identifier"
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

variable "enable_deletion_protection" {
  description = "Enable deletion protection for DynamoDB tables"
  type        = bool
  default     = false
}

variable "enable_point_in_time_recovery" {
  description = "Enable point-in-time recovery for DynamoDB tables"
  type        = bool
  default     = false
}

variable "tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default     = {}
}
