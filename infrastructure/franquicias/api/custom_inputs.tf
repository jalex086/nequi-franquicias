# Variables específicas del microservicio franquicias
# Configuraciones que pueden variar por ambiente o requerimientos específicos

variable "enable_auto_scaling" {
  description = "Enable auto scaling for ECS service"
  type        = bool
  default     = true
}

variable "health_check_path" {
  description = "Health check path for the application"
  type        = string
  default     = "/actuator/health"
}

variable "container_port" {
  description = "Port exposed by the container"
  type        = number
  default     = 8080
}

variable "log_retention_days" {
  description = "CloudWatch log retention in days"
  type        = number
  default     = 7
}

variable "enable_deletion_protection" {
  description = "Enable deletion protection for ALB"
  type        = bool
  default     = false
}

variable "cpu_utilization_threshold" {
  description = "CPU utilization threshold for auto scaling"
  type        = number
  default     = 70
}

variable "memory_utilization_threshold" {
  description = "Memory utilization threshold for auto scaling"
  type        = number
  default     = 80
}
