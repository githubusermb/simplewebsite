













































































































package com.shopcart.handlers.cart;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Cart;
import com.shopcart.models.CartItem;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for deleting a cart item
 */
public class DeleteCartItemHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteCartItemHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CARTS_TABLE = System.getenv("CARTS_TABLE");
    private static final String CART_ITEMS_TABLE = System.getenv("CART_ITEMS_TABLE");

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
            // Get the cart ID and product ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("cartId") || !pathParameters.containsKey("productId")) {
                logger.error("Cart ID and Product ID are required");
                Map<String, Object> response = ApiResponse.badRequest("Cart ID and Product ID are required");
                return convertToApiGatewayResponse(response);
            }
            
            String cartId = pathParameters.get("cartId");
            String productId = pathParameters.get("productId");
            
            // Get the cart from DynamoDB
            logger.info("Getting cart with ID: {}", cartId);
            Cart cart = DynamoDBUtil.getItem(CARTS_TABLE, "cartId", cartId, Cart.class);
            
            if (cart == null) {
                logger.error("Cart with ID {} not found", cartId);
                Map<String, Object> response = ApiResponse.notFound("Cart with ID " + cartId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Get the cart item from DynamoDB
            logger.info("Getting cart item with cart ID: {} and product ID: {}", cartId, productId);
            CartItem cartItem = DynamoDBUtil.getItem(
                CART_ITEMS_TABLE, "cartId", cartId, "productId", productId, CartItem.class);
            
            if (cartItem == null) {
                logger.error("Cart item with cart ID {} and product ID {} not found", cartId, productId);
                Map<String, Object> response = ApiResponse.notFound(
                    "Cart item with cart ID " + cartId + " and product ID " + productId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Delete the cart item from DynamoDB
            logger.info("Deleting cart item with cart ID: {} and product ID: {}", cartId, productId);
            DynamoDBUtil.deleteItem(
                CART_ITEMS_TABLE, "cartId", cartId, "productId", productId, CartItem.class);
            
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
            cart.setUpdatedAt(Instant.now().toString());
            
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
            logger.error("Error deleting cart item: {}", e.getMessage(), e);
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













































































































