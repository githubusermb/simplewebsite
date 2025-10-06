


















































































































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
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lambda function handler for clearing a cart
 */
public class ClearCartHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ClearCartHandler.class);
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
            // Get the cart ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("cartId")) {
                logger.error("Cart ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Cart ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String cartId = pathParameters.get("cartId");
            
            // Get the cart from DynamoDB
            logger.info("Getting cart with ID: {}", cartId);
            Cart cart = DynamoDBUtil.getItem(CARTS_TABLE, "cartId", cartId, Cart.class);
            
            if (cart == null) {
                logger.error("Cart with ID {} not found", cartId);
                Map<String, Object> response = ApiResponse.notFound("Cart with ID " + cartId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Get all cart items
            logger.info("Getting all cart items for cart ID: {}", cartId);
            List<CartItem> cartItems = DynamoDBUtil.queryItemsByIndex(
                CART_ITEMS_TABLE, "CartIndex", "cartId", cartId, CartItem.class);
            
            if (!cartItems.isEmpty()) {
                // Delete all cart items
                logger.info("Deleting {} cart items", cartItems.size());
                List<Key> keys = cartItems.stream()
                    .map(item -> Key.builder()
                        .partitionValue(item.getCartId())
                        .sortValue(item.getProductId())
                        .build())
                    .collect(Collectors.toList());
                
                DynamoDBUtil.batchDeleteItems(CART_ITEMS_TABLE, keys, CartItem.class);
            }
            
            // Update the cart totals
            cart.setTotalPrice(0.0);
            cart.setTotalItems(0);
            cart.setUpdatedAt(Instant.now().toString());
            
            // Save the updated cart to DynamoDB
            logger.info("Saving updated cart: {}", cart);
            DynamoDBUtil.updateItem(CARTS_TABLE, cart);
            
            // Return the updated cart and empty items list
            Map<String, Object> cartData = new HashMap<>();
            cartData.put("cart", cart);
            cartData.put("items", new ArrayList<>());
            
            Map<String, Object> response = ApiResponse.success(cartData);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error clearing cart: {}", e.getMessage(), e);
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


















































































































