# Outputs del módulo transversal DynamoDB
# Para que otros módulos puedan referenciar las tablas

output "franquicias_table_name" {
  description = "Name of the franquicias DynamoDB table"
  value       = aws_dynamodb_table.franquicias.name
}

output "franquicias_table_arn" {
  description = "ARN of the franquicias DynamoDB table"
  value       = aws_dynamodb_table.franquicias.arn
}

output "sucursales_table_name" {
  description = "Name of the sucursales DynamoDB table"
  value       = aws_dynamodb_table.sucursales.name
}

output "sucursales_table_arn" {
  description = "ARN of the sucursales DynamoDB table"
  value       = aws_dynamodb_table.sucursales.arn
}

output "productos_table_name" {
  description = "Name of the productos DynamoDB table"
  value       = aws_dynamodb_table.productos.name
}

output "productos_table_arn" {
  description = "ARN of the productos DynamoDB table"
  value       = aws_dynamodb_table.productos.arn
}

output "all_table_arns" {
  description = "List of all DynamoDB table ARNs for IAM policies"
  value = [
    aws_dynamodb_table.franquicias.arn,
    aws_dynamodb_table.sucursales.arn,
    aws_dynamodb_table.productos.arn,
    "${aws_dynamodb_table.franquicias.arn}/index/*",
    "${aws_dynamodb_table.sucursales.arn}/index/*",
    "${aws_dynamodb_table.productos.arn}/index/*"
  ]
}
