# Variables para ambiente dev
env           = "dev"
capacity      = "business"
functionality = "franquicias"
owner         = "platform-team"
project       = "franchise-api"

# Configuraciones específicas para dev
enable_auto_scaling         = true
log_retention_days         = 7
enable_deletion_protection = false

tags = {
  Environment   = "dev"
  Project      = "franchise-api"
  Owner        = "platform-team"
  Capacity     = "business"
  Functionality = "franquicias"
  Module       = "franquicias-api"
  ManagedBy    = "terraform"
}
