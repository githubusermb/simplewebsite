


# Main Terraform configuration file for the Shopping Cart application

# This file serves as the entry point for the Terraform configuration
# The actual resources are defined in separate files for better organization:
# - providers.tf: AWS provider configuration
# - variables.tf: Input variables
# - dynamodb.tf: DynamoDB tables
# - api_gateway.tf: API Gateway configuration
# - lambda_customers.tf: Lambda functions for customer operations
# - outputs.tf: Output values

# Local variables
locals {
  common_tags = {
    Project     = "ShoppingCart"
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

# Example of how to use the local variables
resource "aws_cloudwatch_log_group" "lambda_log_group" {
  name              = "/aws/lambda/ShoppingCart-${var.environment}"
  retention_in_days = 14
  
  tags = merge(
    local.common_tags,
    {
      Name = "ShoppingCart-Lambda-Logs"
    }
  )
}


