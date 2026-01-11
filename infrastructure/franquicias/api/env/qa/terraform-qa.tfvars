# Variables para ambiente qa
env           = "qa"
capacity      = "business"
functionality = "franquicias"
owner         = "platform-team"
project       = "franchise-api"

# Configuraciones específicas para qa
enable_auto_scaling         = true
log_retention_days         = 14
enable_deletion_protection = false

tags = {
  Environment   = "qa"
  Project      = "franchise-api"
  Owner        = "platform-team"
  Capacity     = "business"
  Functionality = "franquicias"
  Module       = "franquicias-api"
  ManagedBy    = "terraform"
  Testing      = "enabled"
}
