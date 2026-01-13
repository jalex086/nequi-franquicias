# Variables para ambiente pdn
env           = "pdn"
capacity      = "business"
functionality = "franquicias"
owner         = "platform-team"
project       = "franchise-api"

# Container configuration
container_image = "jalex86/nequi-franquicias"

# Configuraciones específicas para pdn
enable_auto_scaling         = true
log_retention_days         = 30
enable_deletion_protection = true

tags = {
  Environment   = "pdn"
  Project      = "franchise-api"
  Owner        = "platform-team"
  Capacity     = "business"
  Functionality = "franquicias"
  Module       = "franquicias-api"
  ManagedBy    = "terraform"
  Critical     = "true"
}
