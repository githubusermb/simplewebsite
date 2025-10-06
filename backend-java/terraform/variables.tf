

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
  validation {
    condition     = contains(["dev", "prod"], var.environment)
    error_message = "Environment must be either 'dev' or 'prod'."
  }
}

variable "lambda_memory_size" {
  description = "Memory size for Lambda functions in MB"
  type        = number
  default     = 512
}

variable "lambda_timeout" {
  description = "Timeout for Lambda functions in seconds"
  type        = number
  default     = 30
}

variable "lambda_runtime" {
  description = "Runtime for Lambda functions"
  type        = string
  default     = "java21"
}

variable "lambda_architecture" {
  description = "Architecture for Lambda functions"
  type        = string
  default     = "x86_64"
}

variable "lambda_package_path" {
  description = "Path to the Lambda package"
  type        = string
  default     = "../target/shopcart-1.0.0.jar"
}

variable "api_gateway_stage_name" {
  description = "API Gateway stage name"
  type        = string
  default     = "dev"
}

