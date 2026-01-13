# Variables locales para el módulo específico de franquicias
# Lógica de naming y configuración por ambiente

locals {
  # Naming convention
  service_name      = "${var.capacity}-${var.functionality}-${var.env}"
  cluster_name      = "${var.capacity}-cluster-${var.env}"
  task_family       = "${var.capacity}-${var.functionality}-${var.env}"
  container_name    = "${var.functionality}-app"
  alb_name          = "${var.capacity}-${var.functionality}-alb-${var.env}"
  target_group_name = "${var.capacity}-${var.functionality}-tg-${var.env}"
  ecr_name          = "${var.capacity}-${var.functionality}"

  # Container configuration
  container_image = "${var.container_image}:${var.env}"

  # Resource sizing by environment
  cpu = var.env == "prod" ? 1024 : 1024
  memory = var.env == "prod" ? 2048 : 2048

  # Scaling configuration
  desired_count = var.env == "prod" ? 2 : 1
  min_capacity  = var.env == "prod" ? 2 : 1
  max_capacity  = var.env == "prod" ? 10 : 3

  # Common tags
  common_tags = {
    Environment   = var.env
    Project      = var.project
    Owner        = var.owner
    Capacity     = var.capacity
    Functionality = var.functionality
    ManagedBy    = "terraform"
  }
}
