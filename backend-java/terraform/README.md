

# Shopping Cart Application - Terraform Configuration

This directory contains the Terraform configuration for deploying the Shopping Cart application to AWS.

## Prerequisites

- Terraform 1.0.0 or later
- AWS CLI configured with appropriate permissions
- Java 21 (Amazon Corretto JDK)
- Maven

## Directory Structure

```
terraform/
├── api_gateway.tf       # API Gateway configuration
├── dynamodb.tf          # DynamoDB tables configuration
├── lambda_customers.tf  # Lambda functions for customer operations
├── modules/             # Reusable Terraform modules
│   └── api_gateway_cors/  # Module for API Gateway CORS configuration
├── outputs.tf           # Output values
├── providers.tf         # Provider configuration
├── variables.tf         # Input variables
└── README.md            # This file
```

## Usage

### Build the Java Application

Before deploying with Terraform, you need to build the Java application:

```bash
cd ../
mvn clean package
```

This will create a JAR file in the `target` directory.

### Initialize Terraform

```bash
cd terraform
terraform init
```

### Plan the Deployment

```bash
terraform plan -out=tfplan
```

### Apply the Deployment

```bash
terraform apply tfplan
```

### Destroy the Deployment

```bash
terraform destroy
```

## Variables

| Name | Description | Default |
|------|-------------|---------|
| aws_region | AWS region for all resources | us-east-1 |
| environment | Environment name | dev |
| lambda_memory_size | Memory size for Lambda functions in MB | 512 |
| lambda_timeout | Timeout for Lambda functions in seconds | 30 |
| lambda_runtime | Runtime for Lambda functions | java21 |
| lambda_architecture | Architecture for Lambda functions | x86_64 |
| lambda_package_path | Path to the Lambda package | ../target/shopcart-1.0.0.jar |
| api_gateway_stage_name | API Gateway stage name | dev |

## Outputs

| Name | Description |
|------|-------------|
| api_gateway_url | API Gateway URL |
| customers_table_name | Customers DynamoDB table name |
| products_table_name | Products DynamoDB table name |
| categories_table_name | Categories DynamoDB table name |
| carts_table_name | Carts DynamoDB table name |
| cart_items_table_name | CartItems DynamoDB table name |
| orders_table_name | Orders DynamoDB table name |
| order_items_table_name | OrderItems DynamoDB table name |

