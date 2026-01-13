terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  # Backend será configurado por ambiente en terraform init
}

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = merge(var.tags, {
      Project   = "nequi-franquicias"
      ManagedBy = "terraform"
      Module    = "franquicias-api"
    })
  }
}
