# DynamoDB Tables - Transversal Business Module
# Tablas compartidas para múltiples microservicios

# Tabla principal de franquicias
resource "aws_dynamodb_table" "franquicias" {
  name           = "business-franquicias-${var.env}"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "PK"
  range_key      = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  attribute {
    name = "GSI1PK"
    type = "S"
  }

  attribute {
    name = "GSI1SK"
    type = "S"
  }

  global_secondary_index {
    name            = "GSI1"
    hash_key        = "GSI1PK"
    range_key       = "GSI1SK"
    projection_type = "ALL"
  }

  point_in_time_recovery {
    enabled = var.env == "prod" ? true : false
  }

  server_side_encryption {
    enabled = true
  }

  tags = merge(var.tags, {
    Name        = "business-franquicias-${var.env}"
    DataType    = "Franquicias"
    Module      = "transversal"
    Capacity    = var.capacity
    Environment = var.env
  })
}

# Tabla de sucursales (modelo híbrido)
resource "aws_dynamodb_table" "sucursales" {
  name           = "business-sucursales-${var.env}"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "PK"
  range_key      = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  attribute {
    name = "GSI1PK"
    type = "S"
  }

  attribute {
    name = "GSI1SK"
    type = "S"
  }

  global_secondary_index {
    name            = "GSI1"
    hash_key        = "GSI1PK"
    range_key       = "GSI1SK"
    projection_type = "ALL"
  }

  point_in_time_recovery {
    enabled = var.env == "prod" ? true : false
  }

  server_side_encryption {
    enabled = true
  }

  tags = merge(var.tags, {
    Name        = "business-sucursales-${var.env}"
    DataType    = "Sucursales"
    Module      = "transversal"
    Capacity    = var.capacity
    Environment = var.env
  })
}

# Tabla de productos (para sucursales grandes)
resource "aws_dynamodb_table" "productos" {
  name           = "business-productos-${var.env}"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "PK"
  range_key      = "SK"

  attribute {
    name = "PK"
    type = "S"
  }

  attribute {
    name = "SK"
    type = "S"
  }

  attribute {
    name = "GSI1PK"
    type = "S"
  }

  attribute {
    name = "GSI1SK"
    type = "S"
  }

  attribute {
    name = "GSI2PK"
    type = "S"
  }

  # GSI1: Buscar productos por sucursal
  global_secondary_index {
    name            = "GSI1"
    hash_key        = "GSI1PK"  # branchId
    range_key       = "GSI1SK"  # productId
    projection_type = "ALL"
  }

  # GSI2: Buscar producto por ID
  global_secondary_index {
    name            = "GSI2"
    hash_key        = "GSI2PK"  # productId
    projection_type = "ALL"
  }

  point_in_time_recovery {
    enabled = var.env == "prod" ? true : false
  }

  server_side_encryption {
    enabled = true
  }

  tags = merge(var.tags, {
    Name        = "business-productos-${var.env}"
    DataType    = "Productos"
    Module      = "transversal"
    Capacity    = var.capacity
    Environment = var.env
  })
}
