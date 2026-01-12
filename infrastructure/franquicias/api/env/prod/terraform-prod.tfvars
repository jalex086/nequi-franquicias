# Variables para ambiente prod
env           = "prod"
capacity      = "business"
functionality = "franquicias"
owner         = "platform-team"
project       = "franchise-api"

# Container configuration
container_image = "jalex86/nequi-franquicias"

# Configuraciones específicas para prod
enable_auto_scaling         = true
log_retention_days         = 30
enable_deletion_protection = true
cpu_utilization_threshold  = 60
memory_utilization_threshold = 70

tags = {
  Environment   = "prod"
  Project      = "franchise-api"
  Owner        = "platform-team"
  Capacity     = "business"
  Functionality = "franquicias"
  Module       = "franquicias-api"
  ManagedBy    = "terraform"
  Backup       = "enabled"
  Monitoring   = "enhanced"
  Compliance   = "required"
}
