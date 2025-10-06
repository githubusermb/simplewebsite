


# API Gateway
resource "aws_api_gateway_rest_api" "shopping_cart_api" {
  name        = "ShoppingCartAPI-${var.environment}"
  description = "Shopping Cart API for ${var.environment} environment"

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# API Gateway Deployment
resource "aws_api_gateway_deployment" "shopping_cart_api_deployment" {
  depends_on = [
    # All API Gateway resources should be listed here
    aws_api_gateway_integration.get_customers_integration,
    aws_api_gateway_integration.get_customer_integration,
    aws_api_gateway_integration.create_customer_integration,
    aws_api_gateway_integration.update_customer_integration,
    aws_api_gateway_integration.delete_customer_integration,
    aws_api_gateway_integration.login_integration,
    aws_api_gateway_integration.get_products_integration,
    aws_api_gateway_integration.get_product_integration,
    aws_api_gateway_integration.get_products_by_category_integration,
    aws_api_gateway_integration.create_product_integration,
    aws_api_gateway_integration.update_product_integration,
    aws_api_gateway_integration.delete_product_integration,
    aws_api_gateway_integration.get_categories_integration,
    aws_api_gateway_integration.get_category_integration,
    aws_api_gateway_integration.create_category_integration,
    aws_api_gateway_integration.update_category_integration,
    aws_api_gateway_integration.delete_category_integration,
    aws_api_gateway_integration.get_cart_integration,
    aws_api_gateway_integration.get_customer_cart_integration,
    aws_api_gateway_integration.create_cart_integration,
    aws_api_gateway_integration.add_cart_item_integration,
    aws_api_gateway_integration.update_cart_item_integration,
    aws_api_gateway_integration.delete_cart_item_integration,
    aws_api_gateway_integration.clear_cart_integration,
    aws_api_gateway_integration.get_orders_integration,
    aws_api_gateway_integration.get_order_integration,
    aws_api_gateway_integration.get_customer_orders_integration,
    aws_api_gateway_integration.create_order_integration,
    aws_api_gateway_integration.update_order_status_integration,
    aws_api_gateway_integration.delete_order_integration
  ]

  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  stage_name  = var.api_gateway_stage_name

  lifecycle {
    create_before_destroy = true
  }
}

# Enable CORS for API Gateway
module "cors" {
  source = "./modules/api_gateway_cors"

  api_id          = aws_api_gateway_rest_api.shopping_cart_api.id
  api_resource_id = aws_api_gateway_rest_api.shopping_cart_api.root_resource_id
}

# API Gateway Resources
# Customers Resource
resource "aws_api_gateway_resource" "customers_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_rest_api.shopping_cart_api.root_resource_id
  path_part   = "customers"
}

resource "aws_api_gateway_resource" "customer_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.customers_resource.id
  path_part   = "{customerId}"
}

# Products Resource
resource "aws_api_gateway_resource" "products_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_rest_api.shopping_cart_api.root_resource_id
  path_part   = "products"
}

resource "aws_api_gateway_resource" "product_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.products_resource.id
  path_part   = "{productId}"
}

# Categories Resource
resource "aws_api_gateway_resource" "categories_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_rest_api.shopping_cart_api.root_resource_id
  path_part   = "categories"
}

resource "aws_api_gateway_resource" "category_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.categories_resource.id
  path_part   = "{categoryId}"
}

resource "aws_api_gateway_resource" "category_products_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.category_resource.id
  path_part   = "products"
}

# Carts Resource
resource "aws_api_gateway_resource" "carts_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_rest_api.shopping_cart_api.root_resource_id
  path_part   = "carts"
}

resource "aws_api_gateway_resource" "cart_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.carts_resource.id
  path_part   = "{cartId}"
}

resource "aws_api_gateway_resource" "cart_items_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.cart_resource.id
  path_part   = "items"
}

resource "aws_api_gateway_resource" "cart_item_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.cart_items_resource.id
  path_part   = "{productId}"
}

resource "aws_api_gateway_resource" "cart_clear_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.cart_resource.id
  path_part   = "clear"
}

resource "aws_api_gateway_resource" "customer_cart_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.customer_resource.id
  path_part   = "cart"
}

# Orders Resource
resource "aws_api_gateway_resource" "orders_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_rest_api.shopping_cart_api.root_resource_id
  path_part   = "orders"
}

resource "aws_api_gateway_resource" "order_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.orders_resource.id
  path_part   = "{orderId}"
}

resource "aws_api_gateway_resource" "order_status_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.order_resource.id
  path_part   = "status"
}

resource "aws_api_gateway_resource" "customer_orders_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_resource.customer_resource.id
  path_part   = "orders"
}

# Login Resource
resource "aws_api_gateway_resource" "login_resource" {
  rest_api_id = aws_api_gateway_rest_api.shopping_cart_api.id
  parent_id   = aws_api_gateway_rest_api.shopping_cart_api.root_resource_id
  path_part   = "login"
}

