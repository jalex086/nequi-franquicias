# Variables para ambiente pdn
env           = "pdn"
capacity      = "business"
functionality = "franquicias"
owner         = "platform-team"
project       = "franchise-api"

# Configuraciones específicas para pdn
enable_deletion_protection = true
enable_point_in_time_recovery = true

tags = {
  Environment   = "pdn"
  Project      = "franchise-api"
  Owner        = "platform-team"
  Capacity     = "business"
  Functionality = "franquicias"
  ManagedBy    = "terraform"
  Critical     = "true"
}
