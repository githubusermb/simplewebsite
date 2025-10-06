

# DynamoDB Tables

# Customers Table
resource "aws_dynamodb_table" "customers_table" {
  name         = "Customers-${var.environment}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "customerId"

  attribute {
    name = "customerId"
    type = "S"
  }

  attribute {
    name = "email"
    type = "S"
  }

  global_secondary_index {
    name            = "EmailIndex"
    hash_key        = "email"
    projection_type = "ALL"
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# Products Table
resource "aws_dynamodb_table" "products_table" {
  name         = "Products-${var.environment}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "productId"

  attribute {
    name = "productId"
    type = "S"
  }

  attribute {
    name = "categoryId"
    type = "S"
  }

  global_secondary_index {
    name            = "CategoryIndex"
    hash_key        = "categoryId"
    projection_type = "ALL"
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# Categories Table
resource "aws_dynamodb_table" "categories_table" {
  name         = "Categories-${var.environment}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "categoryId"

  attribute {
    name = "categoryId"
    type = "S"
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# Carts Table
resource "aws_dynamodb_table" "carts_table" {
  name         = "Carts-${var.environment}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "cartId"

  attribute {
    name = "cartId"
    type = "S"
  }

  attribute {
    name = "customerId"
    type = "S"
  }

  global_secondary_index {
    name            = "CustomerIndex"
    hash_key        = "customerId"
    projection_type = "ALL"
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# CartItems Table
resource "aws_dynamodb_table" "cart_items_table" {
  name         = "CartItems-${var.environment}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "cartId"
  range_key    = "productId"

  attribute {
    name = "cartId"
    type = "S"
  }

  attribute {
    name = "productId"
    type = "S"
  }

  global_secondary_index {
    name            = "CartIndex"
    hash_key        = "cartId"
    projection_type = "ALL"
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# Orders Table
resource "aws_dynamodb_table" "orders_table" {
  name         = "Orders-${var.environment}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "orderId"

  attribute {
    name = "orderId"
    type = "S"
  }

  attribute {
    name = "customerId"
    type = "S"
  }

  global_secondary_index {
    name            = "CustomerIndex"
    hash_key        = "customerId"
    projection_type = "ALL"
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# OrderItems Table
resource "aws_dynamodb_table" "order_items_table" {
  name         = "OrderItems-${var.environment}"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "orderId"
  range_key    = "productId"

  attribute {
    name = "orderId"
    type = "S"
  }

  attribute {
    name = "productId"
    type = "S"
  }

  global_secondary_index {
    name            = "OrderIndex"
    hash_key        = "orderId"
    projection_type = "ALL"
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

