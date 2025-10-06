


output "api_gateway_url" {
  description = "API Gateway URL"
  value       = "${aws_api_gateway_deployment.shopping_cart_api_deployment.invoke_url}"
}

output "customers_table_name" {
  description = "Customers DynamoDB table name"
  value       = aws_dynamodb_table.customers_table.name
}

output "products_table_name" {
  description = "Products DynamoDB table name"
  value       = aws_dynamodb_table.products_table.name
}

output "categories_table_name" {
  description = "Categories DynamoDB table name"
  value       = aws_dynamodb_table.categories_table.name
}

output "carts_table_name" {
  description = "Carts DynamoDB table name"
  value       = aws_dynamodb_table.carts_table.name
}

output "cart_items_table_name" {
  description = "CartItems DynamoDB table name"
  value       = aws_dynamodb_table.cart_items_table.name
}

output "orders_table_name" {
  description = "Orders DynamoDB table name"
  value       = aws_dynamodb_table.orders_table.name
}

output "order_items_table_name" {
  description = "OrderItems DynamoDB table name"
  value       = aws_dynamodb_table.order_items_table.name
}


