# Data Models for Shopping Cart Application

## Customers
- **customerId**: String (Primary Key) - UUID for customer
- **email**: String - Customer's email address (unique)
- **firstName**: String - Customer's first name
- **lastName**: String - Customer's last name
- **address**: Object - Customer's address
  - **street**: String - Street address
  - **city**: String - City
  - **state**: String - State/Province
  - **zipCode**: String - Postal/ZIP code
  - **country**: String - Country
- **phoneNumber**: String - Customer's phone number
- **createdAt**: String - ISO timestamp of account creation
- **updatedAt**: String - ISO timestamp of last update

## Products
- **productId**: String (Primary Key) - UUID for product
- **name**: String - Product name
- **description**: String - Product description
- **price**: Number - Product price
- **categoryId**: String - Reference to category
- **imageUrl**: String - URL to product image
- **inventory**: Number - Available inventory count
- **createdAt**: String - ISO timestamp of product creation
- **updatedAt**: String - ISO timestamp of last update

## Categories
- **categoryId**: String (Primary Key) - UUID for category
- **name**: String - Category name
- **description**: String - Category description
- **imageUrl**: String - URL to category image
- **createdAt**: String - ISO timestamp of category creation
- **updatedAt**: String - ISO timestamp of last update

## Carts
- **cartId**: String (Primary Key) - UUID for cart
- **customerId**: String - Reference to customer
- **status**: String - Cart status (active, abandoned, converted)
- **createdAt**: String - ISO timestamp of cart creation
- **updatedAt**: String - ISO timestamp of last update
- **totalItems**: Number - Total number of items in cart
- **totalPrice**: Number - Total price of all items in cart

## CartItems
- **cartId**: String (Partition Key) - Reference to cart
- **productId**: String (Sort Key) - Reference to product
- **quantity**: Number - Quantity of product
- **price**: Number - Price at time of adding to cart
- **addedAt**: String - ISO timestamp when item was added

## Orders
- **orderId**: String (Primary Key) - UUID for order
- **customerId**: String - Reference to customer
- **orderDate**: String - ISO timestamp of order placement
- **status**: String - Order status (pending, processing, shipped, delivered, cancelled)
- **shippingAddress**: Object - Shipping address
  - **street**: String - Street address
  - **city**: String - City
  - **state**: String - State/Province
  - **zipCode**: String - Postal/ZIP code
  - **country**: String - Country
- **billingAddress**: Object - Billing address (same structure as shipping)
- **paymentMethod**: String - Payment method used
- **totalAmount**: Number - Total order amount
- **tax**: Number - Tax amount
- **shippingCost**: Number - Shipping cost
- **createdAt**: String - ISO timestamp of order creation
- **updatedAt**: String - ISO timestamp of last update

## OrderItems
- **orderId**: String (Partition Key) - Reference to order
- **productId**: String (Sort Key) - Reference to product
- **quantity**: Number - Quantity of product
- **price**: Number - Price at time of order
