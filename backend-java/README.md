# Shopping Cart Application - Java Backend

This is the Java backend for the Shopping Cart application using AWS native services. It provides REST APIs for managing customers, products, categories, carts, and orders.

## Project Structure

```
backend-java/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── shopcart/
│       │           ├── handlers/
│       │           │   ├── cart/
│       │           │   ├── categories/
│       │           │   ├── customers/
│       │           │   ├── orders/
│       │           │   └── products/
│       │           ├── models/
│       │           └── utils/
│       └── resources/
├── target/
├── infrastructure/
│   └── template.yaml
├── pom.xml
└── README.md
```

## Technologies Used

- Java 21 (Amazon Corretto JDK)
- AWS Lambda
- Amazon DynamoDB
- Amazon API Gateway
- AWS Serverless Application Model (SAM)
- Maven

## Prerequisites

- Java 21 or later
- Maven
- AWS CLI
- AWS SAM CLI

## Building the Application

To build the application, run the following command:

```bash
mvn clean package
```

This will create a JAR file in the `target` directory.

## Deploying the Application

To deploy the application to AWS, run the following commands:

```bash
cd infrastructure
sam build
sam deploy --guided
```

Follow the prompts to deploy the application.

## API Endpoints

### Customers

- `GET /customers` - Get all customers
- `GET /customers/{customerId}` - Get a customer by ID
- `POST /customers` - Create a new customer
- `PUT /customers/{customerId}` - Update a customer
- `DELETE /customers/{customerId}` - Delete a customer
- `POST /login` - Login a customer

### Products

- `GET /products` - Get all products
- `GET /products/{productId}` - Get a product by ID
- `GET /categories/{categoryId}/products` - Get products by category
- `POST /products` - Create a new product
- `PUT /products/{productId}` - Update a product
- `DELETE /products/{productId}` - Delete a product

### Categories

- `GET /categories` - Get all categories
- `GET /categories/{categoryId}` - Get a category by ID
- `POST /categories` - Create a new category
- `PUT /categories/{categoryId}` - Update a category
- `DELETE /categories/{categoryId}` - Delete a category

### Cart

- `GET /carts/{cartId}` - Get a cart by ID
- `GET /customers/{customerId}/cart` - Get a customer's cart
- `POST /carts` - Create a new cart
- `POST /carts/{cartId}/items` - Add an item to a cart
- `PUT /carts/{cartId}/items/{productId}` - Update a cart item
- `DELETE /carts/{cartId}/items/{productId}` - Remove an item from a cart
- `POST /carts/{cartId}/clear` - Clear a cart

### Orders

- `GET /orders` - Get all orders
- `GET /orders/{orderId}` - Get an order by ID
- `GET /customers/{customerId}/orders` - Get a customer's orders
- `POST /orders` - Create a new order
- `PUT /orders/{orderId}/status` - Update an order status
- `DELETE /orders/{orderId}` - Delete an order

## DynamoDB Tables

- `Customers` - Stores customer information
- `Products` - Stores product information
- `Categories` - Stores category information
- `Carts` - Stores cart information
- `CartItems` - Stores cart item information
- `Orders` - Stores order information
- `OrderItems` - Stores order item information

## Environment Variables

The following environment variables are used by the Lambda functions:

- `CUSTOMERS_TABLE` - The name of the Customers DynamoDB table
- `PRODUCTS_TABLE` - The name of the Products DynamoDB table
- `CATEGORIES_TABLE` - The name of the Categories DynamoDB table
- `CARTS_TABLE` - The name of the Carts DynamoDB table
- `CART_ITEMS_TABLE` - The name of the CartItems DynamoDB table
- `ORDERS_TABLE` - The name of the Orders DynamoDB table
- `ORDER_ITEMS_TABLE` - The name of the OrderItems DynamoDB table

## Testing

To run the tests, use the following command:

```bash
mvn test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
