


# IAM Role for Lambda Functions
resource "aws_iam_role" "lambda_role" {
  name = "lambda-role-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# IAM Policy for Lambda to access CloudWatch Logs
resource "aws_iam_policy" "lambda_logging_policy" {
  name        = "lambda-logging-policy-${var.environment}"
  description = "IAM policy for Lambda logging"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:logs:*:*:*"
      }
    ]
  })
}

# Attach the logging policy to the Lambda role
resource "aws_iam_role_policy_attachment" "lambda_logs_attachment" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.lambda_logging_policy.arn
}

# Customer Lambda Functions

# GetCustomer Lambda Function
resource "aws_lambda_function" "get_customers_function" {
  function_name = "GetCustomersFunction-${var.environment}"
  role          = aws_iam_role.lambda_role.arn
  handler       = "com.shopcart.handlers.customers.GetCustomerHandler::handleRequest"
  runtime       = var.lambda_runtime
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  filename      = var.lambda_package_path
  architectures = [var.lambda_architecture]

  environment {
    variables = {
      CUSTOMERS_TABLE = aws_dynamodb_table.customers_table.name
    }
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# IAM Policy for GetCustomer Lambda to access DynamoDB
resource "aws_iam_policy" "get_customers_policy" {
  name        = "get-customers-policy-${var.environment}"
  description = "IAM policy for GetCustomers Lambda to access DynamoDB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:GetItem",
          "dynamodb:Scan",
          "dynamodb:Query"
        ]
        Effect   = "Allow"
        Resource = [
          aws_dynamodb_table.customers_table.arn,
          "${aws_dynamodb_table.customers_table.arn}/index/*"
        ]
      }
    ]
  })
}

# Attach the DynamoDB policy to the Lambda role
resource "aws_iam_role_policy_attachment" "get_customers_attachment" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.get_customers_policy.arn
}

# API Gateway Method for GetCustomers
resource "aws_api_gateway_method" "get_customers_method" {
  rest_api_id   = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id   = aws_api_gateway_resource.customers_resource.id
  http_method   = "GET"
  authorization_type = "NONE"
}

# API Gateway Integration for GetCustomers
resource "aws_api_gateway_integration" "get_customers_integration" {
  rest_api_id             = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id             = aws_api_gateway_resource.customers_resource.id
  http_method             = aws_api_gateway_method.get_customers_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.get_customers_function.invoke_arn
}

# Lambda Permission for API Gateway to invoke GetCustomers
resource "aws_lambda_permission" "get_customers_permission" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.get_customers_function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.shopping_cart_api.execution_arn}/*/${aws_api_gateway_method.get_customers_method.http_method}${aws_api_gateway_resource.customers_resource.path}"
}

# API Gateway Method for GetCustomer by ID
resource "aws_api_gateway_method" "get_customer_method" {
  rest_api_id   = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id   = aws_api_gateway_resource.customer_resource.id
  http_method   = "GET"
  authorization_type = "NONE"
}

# API Gateway Integration for GetCustomer by ID
resource "aws_api_gateway_integration" "get_customer_integration" {
  rest_api_id             = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id             = aws_api_gateway_resource.customer_resource.id
  http_method             = aws_api_gateway_method.get_customer_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.get_customers_function.invoke_arn
}

# Lambda Permission for API Gateway to invoke GetCustomer by ID
resource "aws_lambda_permission" "get_customer_permission" {
  statement_id  = "AllowAPIGatewayInvokeGetCustomer"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.get_customers_function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.shopping_cart_api.execution_arn}/*/${aws_api_gateway_method.get_customer_method.http_method}${aws_api_gateway_resource.customer_resource.path}"
}

# CreateCustomer Lambda Function
resource "aws_lambda_function" "create_customer_function" {
  function_name = "CreateCustomerFunction-${var.environment}"
  role          = aws_iam_role.lambda_role.arn
  handler       = "com.shopcart.handlers.customers.CreateCustomerHandler::handleRequest"
  runtime       = var.lambda_runtime
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  filename      = var.lambda_package_path
  architectures = [var.lambda_architecture]

  environment {
    variables = {
      CUSTOMERS_TABLE = aws_dynamodb_table.customers_table.name
    }
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# IAM Policy for CreateCustomer Lambda to access DynamoDB
resource "aws_iam_policy" "create_customer_policy" {
  name        = "create-customer-policy-${var.environment}"
  description = "IAM policy for CreateCustomer Lambda to access DynamoDB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:Query"
        ]
        Effect   = "Allow"
        Resource = [
          aws_dynamodb_table.customers_table.arn,
          "${aws_dynamodb_table.customers_table.arn}/index/*"
        ]
      }
    ]
  })
}

# Attach the DynamoDB policy to the Lambda role
resource "aws_iam_role_policy_attachment" "create_customer_attachment" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.create_customer_policy.arn
}

# API Gateway Method for CreateCustomer
resource "aws_api_gateway_method" "create_customer_method" {
  rest_api_id   = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id   = aws_api_gateway_resource.customers_resource.id
  http_method   = "POST"
  authorization_type = "NONE"
}

# API Gateway Integration for CreateCustomer
resource "aws_api_gateway_integration" "create_customer_integration" {
  rest_api_id             = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id             = aws_api_gateway_resource.customers_resource.id
  http_method             = aws_api_gateway_method.create_customer_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.create_customer_function.invoke_arn
}

# Lambda Permission for API Gateway to invoke CreateCustomer
resource "aws_lambda_permission" "create_customer_permission" {
  statement_id  = "AllowAPIGatewayInvokeCreateCustomer"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.create_customer_function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.shopping_cart_api.execution_arn}/*/${aws_api_gateway_method.create_customer_method.http_method}${aws_api_gateway_resource.customers_resource.path}"
}

# UpdateCustomer Lambda Function
resource "aws_lambda_function" "update_customer_function" {
  function_name = "UpdateCustomerFunction-${var.environment}"
  role          = aws_iam_role.lambda_role.arn
  handler       = "com.shopcart.handlers.customers.UpdateCustomerHandler::handleRequest"
  runtime       = var.lambda_runtime
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  filename      = var.lambda_package_path
  architectures = [var.lambda_architecture]

  environment {
    variables = {
      CUSTOMERS_TABLE = aws_dynamodb_table.customers_table.name
    }
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# IAM Policy for UpdateCustomer Lambda to access DynamoDB
resource "aws_iam_policy" "update_customer_policy" {
  name        = "update-customer-policy-${var.environment}"
  description = "IAM policy for UpdateCustomer Lambda to access DynamoDB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:UpdateItem",
          "dynamodb:GetItem"
        ]
        Effect   = "Allow"
        Resource = aws_dynamodb_table.customers_table.arn
      }
    ]
  })
}

# Attach the DynamoDB policy to the Lambda role
resource "aws_iam_role_policy_attachment" "update_customer_attachment" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.update_customer_policy.arn
}

# API Gateway Method for UpdateCustomer
resource "aws_api_gateway_method" "update_customer_method" {
  rest_api_id   = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id   = aws_api_gateway_resource.customer_resource.id
  http_method   = "PUT"
  authorization_type = "NONE"
}

# API Gateway Integration for UpdateCustomer
resource "aws_api_gateway_integration" "update_customer_integration" {
  rest_api_id             = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id             = aws_api_gateway_resource.customer_resource.id
  http_method             = aws_api_gateway_method.update_customer_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.update_customer_function.invoke_arn
}

# Lambda Permission for API Gateway to invoke UpdateCustomer
resource "aws_lambda_permission" "update_customer_permission" {
  statement_id  = "AllowAPIGatewayInvokeUpdateCustomer"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.update_customer_function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.shopping_cart_api.execution_arn}/*/${aws_api_gateway_method.update_customer_method.http_method}${aws_api_gateway_resource.customer_resource.path}"
}

# DeleteCustomer Lambda Function
resource "aws_lambda_function" "delete_customer_function" {
  function_name = "DeleteCustomerFunction-${var.environment}"
  role          = aws_iam_role.lambda_role.arn
  handler       = "com.shopcart.handlers.customers.DeleteCustomerHandler::handleRequest"
  runtime       = var.lambda_runtime
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  filename      = var.lambda_package_path
  architectures = [var.lambda_architecture]

  environment {
    variables = {
      CUSTOMERS_TABLE = aws_dynamodb_table.customers_table.name
    }
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# IAM Policy for DeleteCustomer Lambda to access DynamoDB
resource "aws_iam_policy" "delete_customer_policy" {
  name        = "delete-customer-policy-${var.environment}"
  description = "IAM policy for DeleteCustomer Lambda to access DynamoDB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:DeleteItem",
          "dynamodb:GetItem"
        ]
        Effect   = "Allow"
        Resource = aws_dynamodb_table.customers_table.arn
      }
    ]
  })
}

# Attach the DynamoDB policy to the Lambda role
resource "aws_iam_role_policy_attachment" "delete_customer_attachment" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.delete_customer_policy.arn
}

# API Gateway Method for DeleteCustomer
resource "aws_api_gateway_method" "delete_customer_method" {
  rest_api_id   = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id   = aws_api_gateway_resource.customer_resource.id
  http_method   = "DELETE"
  authorization_type = "NONE"
}

# API Gateway Integration for DeleteCustomer
resource "aws_api_gateway_integration" "delete_customer_integration" {
  rest_api_id             = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id             = aws_api_gateway_resource.customer_resource.id
  http_method             = aws_api_gateway_method.delete_customer_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.delete_customer_function.invoke_arn
}

# Lambda Permission for API Gateway to invoke DeleteCustomer
resource "aws_lambda_permission" "delete_customer_permission" {
  statement_id  = "AllowAPIGatewayInvokeDeleteCustomer"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.delete_customer_function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.shopping_cart_api.execution_arn}/*/${aws_api_gateway_method.delete_customer_method.http_method}${aws_api_gateway_resource.customer_resource.path}"
}

# Login Lambda Function
resource "aws_lambda_function" "login_function" {
  function_name = "LoginFunction-${var.environment}"
  role          = aws_iam_role.lambda_role.arn
  handler       = "com.shopcart.handlers.customers.LoginHandler::handleRequest"
  runtime       = var.lambda_runtime
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  filename      = var.lambda_package_path
  architectures = [var.lambda_architecture]

  environment {
    variables = {
      CUSTOMERS_TABLE = aws_dynamodb_table.customers_table.name
    }
  }

  tags = {
    Environment = var.environment
    Application = "ShopCart"
  }
}

# IAM Policy for Login Lambda to access DynamoDB
resource "aws_iam_policy" "login_policy" {
  name        = "login-policy-${var.environment}"
  description = "IAM policy for Login Lambda to access DynamoDB"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:GetItem",
          "dynamodb:Query"
        ]
        Effect   = "Allow"
        Resource = [
          aws_dynamodb_table.customers_table.arn,
          "${aws_dynamodb_table.customers_table.arn}/index/*"
        ]
      }
    ]
  })
}

# Attach the DynamoDB policy to the Lambda role
resource "aws_iam_role_policy_attachment" "login_attachment" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.login_policy.arn
}

# API Gateway Method for Login
resource "aws_api_gateway_method" "login_method" {
  rest_api_id   = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id   = aws_api_gateway_resource.login_resource.id
  http_method   = "POST"
  authorization_type = "NONE"
}

# API Gateway Integration for Login
resource "aws_api_gateway_integration" "login_integration" {
  rest_api_id             = aws_api_gateway_rest_api.shopping_cart_api.id
  resource_id             = aws_api_gateway_resource.login_resource.id
  http_method             = aws_api_gateway_method.login_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.login_function.invoke_arn
}

# Lambda Permission for API Gateway to invoke Login
resource "aws_lambda_permission" "login_permission" {
  statement_id  = "AllowAPIGatewayInvokeLogin"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.login_function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.shopping_cart_api.execution_arn}/*/${aws_api_gateway_method.login_method.http_method}${aws_api_gateway_resource.login_resource.path}"
}


