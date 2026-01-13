# Variables locales uniformes para todos los ambientes
# Configuración consistente sin diferencias innecesarias

locals {
  # Naming convention uniforme
  service_name      = "${var.capacity}-${var.functionality}-${var.env}"
  cluster_name      = "${var.capacity}-cluster-${var.env}"
  task_family       = "${var.capacity}-${var.functionality}-${var.env}"
  container_name    = "${var.functionality}-app"
  alb_name          = "${var.capacity}-${var.functionality}-alb-${var.env}"
  target_group_name = "${var.capacity}-${var.functionality}-tg-${var.env}"

  # Container configuration uniforme
  container_image = "${var.container_image}:latest-${var.env}"

  # Resource sizing uniforme (simplificado)
  cpu    = 1024
  memory = 2048

  # Scaling configuration por ambiente (solo lo necesario)
  desired_count = var.env == "pdn" ? 2 : 1

  # Log retention por ambiente
  log_retention_days = var.env == "pdn" ? 30 : 7

  # Deletion protection solo en producción
  enable_deletion_protection = var.env == "pdn" ? true : false

  # Common tags uniformes
  common_tags = {
    Environment   = var.env
    Project      = var.project
    Owner        = var.owner
    Capacity     = var.capacity
    Functionality = var.functionality
    Module       = "franquicias-api"
    ManagedBy    = "terraform"
  }
}
