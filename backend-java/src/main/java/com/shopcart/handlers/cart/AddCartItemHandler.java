

























































































package com.shopcart.handlers.cart;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Cart;
import com.shopcart.models.CartItem;
import com.shopcart.models.Product;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for adding an item to a cart
 */
public class AddCartItemHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(AddCartItemHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CARTS_TABLE = System.getenv("CARTS_TABLE");
    private static final String CART_ITEMS_TABLE = System.getenv("CART_ITEMS_TABLE");
    private static final String PRODUCTS_TABLE = System.getenv("PRODUCTS_TABLE");

    /**
     * Handle the Lambda function request
     * @param input The API Gateway request event
     * @param context The Lambda context
     * @return The API Gateway response event
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        logger.info("Received request: {}", input);
        
        try {
            // Get the cart ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("cartId")) {
                logger.error("Cart ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Cart ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String cartId = pathParameters.get("cartId");
            
            // Parse the request body
            String requestBody = input.getBody();
            if (requestBody == null || requestBody.isEmpty()) {
                logger.error("Request body is empty");
                Map<String, Object> response = ApiResponse.badRequest("Request body is required");
                return convertToApiGatewayResponse(response);
            }
            
            Map<String, Object> requestMap = objectMapper.readValue(requestBody, Map.class);
            String productId = (String) requestMap.get("productId");
            Integer quantity = (Integer) requestMap.get("quantity");
            
            if (productId == null || productId.isEmpty()) {
                logger.error("Product ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Product ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (quantity == null || quantity <= 0) {
                logger.error("Quantity must be greater than 0");
                Map<String, Object> response = ApiResponse.badRequest("Quantity must be greater than 0");
                return convertToApiGatewayResponse(response);
            }
            
            // Get the cart from DynamoDB
            logger.info("Getting cart with ID: {}", cartId);
            Cart cart = DynamoDBUtil.getItem(CARTS_TABLE, "cartId", cartId, Cart.class);
            
            if (cart == null) {
                logger.error("Cart with ID {} not found", cartId);
                Map<String, Object> response = ApiResponse.notFound("Cart with ID " + cartId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Get the product from DynamoDB
            logger.info("Getting product with ID: {}", productId);
            Product product = DynamoDBUtil.getItem(PRODUCTS_TABLE, "productId", productId, Product.class);
            
            if (product == null) {
                logger.error("Product with ID {} not found", productId);
                Map<String, Object> response = ApiResponse.notFound("Product with ID " + productId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Check if the product is in stock
            if (product.getStock() < quantity) {
                logger.error("Product {} is out of stock. Available: {}, Requested: {}", 
                           productId, product.getStock(), quantity);
                Map<String, Object> response = ApiResponse.badRequest(
                    "Product is out of stock. Available: " + product.getStock() + ", Requested: " + quantity);
                return convertToApiGatewayResponse(response);
            }
            
            // Check if the item already exists in the cart
            logger.info("Checking if product {} already exists in cart {}", productId, cartId);
            CartItem existingItem = DynamoDBUtil.getItem(
                CART_ITEMS_TABLE, "cartId", cartId, "productId", productId, CartItem.class);
            
            String timestamp = Instant.now().toString();
            CartItem cartItem;
            
            if (existingItem != null) {
                // Update the existing item
                logger.info("Updating existing cart item: {}", existingItem);
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                existingItem.setTotalPrice(existingItem.getPrice() * existingItem.getQuantity());
                existingItem.setUpdatedAt(timestamp);
                cartItem = existingItem;
            } else {
                // Create a new cart item
                cartItem = new CartItem();
                cartItem.setCartId(cartId);
                cartItem.setProductId(productId);
                cartItem.setName(product.getName());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setImageUrl(product.getImageUrl());
                cartItem.setTotalPrice(product.getPrice() * quantity);
                cartItem.setCreatedAt(timestamp);
                cartItem.setUpdatedAt(timestamp);
            }
            
            // Save the cart item to DynamoDB
            logger.info("Saving cart item: {}", cartItem);
            DynamoDBUtil.putItem(CART_ITEMS_TABLE, cartItem);
            
            // Update the cart totals
            logger.info("Updating cart totals");
            List<CartItem> cartItems = DynamoDBUtil.queryItemsByIndex(
                CART_ITEMS_TABLE, "CartIndex", "cartId", cartId, CartItem.class);
            
            double totalPrice = 0.0;
            int totalItems = 0;
            
            for (CartItem item : cartItems) {
                totalPrice += item.getTotalPrice();
                totalItems += item.getQuantity();
            }
            
            cart.setTotalPrice(totalPrice);
            cart.setTotalItems(totalItems);
            cart.setUpdatedAt(timestamp);
            
            // Save the updated cart to DynamoDB
            logger.info("Saving updated cart: {}", cart);
            DynamoDBUtil.updateItem(CARTS_TABLE, cart);
            
            // Return the updated cart and items
            Map<String, Object> cartData = new HashMap<>();
            cartData.put("cart", cart);
            cartData.put("items", cartItems);
            
            Map<String, Object> response = ApiResponse.success(cartData);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error adding item to cart: {}", e.getMessage(), e);
            Map<String, Object> response = ApiResponse.serverError(e.getMessage());
            return convertToApiGatewayResponse(response);
        }
    }
    
    /**
     * Convert the API response to an API Gateway response event
     * @param response The API response
     * @return The API Gateway response event
     */
    private APIGatewayProxyResponseEvent convertToApiGatewayResponse(Map<String, Object> response) {
        APIGatewayProxyResponseEvent apiGatewayResponse = new APIGatewayProxyResponseEvent();
        apiGatewayResponse.setStatusCode((Integer) response.get("statusCode"));
        apiGatewayResponse.setHeaders((Map<String, String>) response.get("headers"));
        apiGatewayResponse.setBody((String) response.get("body"));
        return apiGatewayResponse;
    }
}

























































































