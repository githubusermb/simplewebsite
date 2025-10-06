


















































































package com.shopcart.handlers.cart;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Cart;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Lambda function handler for creating a cart
 */
public class CreateCartHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CreateCartHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CARTS_TABLE = System.getenv("CARTS_TABLE");

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
            // Parse the request body
            String requestBody = input.getBody();
            if (requestBody == null || requestBody.isEmpty()) {
                logger.error("Request body is empty");
                Map<String, Object> response = ApiResponse.badRequest("Request body is required");
                return convertToApiGatewayResponse(response);
            }
            
            Map<String, String> requestMap = objectMapper.readValue(requestBody, Map.class);
            String customerId = requestMap.get("customerId");
            
            if (customerId == null || customerId.isEmpty()) {
                logger.error("Customer ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Customer ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            // Check if the customer already has a cart
            logger.info("Checking if customer {} already has a cart", customerId);
            List<Cart> existingCarts = DynamoDBUtil.queryItemsByIndex(
                CARTS_TABLE, "CustomerIndex", "customerId", customerId, Cart.class);
            
            if (!existingCarts.isEmpty()) {
                logger.info("Customer {} already has a cart: {}", customerId, existingCarts.get(0));
                Map<String, Object> response = ApiResponse.success(existingCarts.get(0));
                return convertToApiGatewayResponse(response);
            }
            
            // Create a new cart
            String cartId = UUID.randomUUID().toString();
            String timestamp = Instant.now().toString();
            
            Cart cart = new Cart();
            cart.setCartId(cartId);
            cart.setCustomerId(customerId);
            cart.setTotalPrice(0.0);
            cart.setTotalItems(0);
            cart.setCreatedAt(timestamp);
            cart.setUpdatedAt(timestamp);
            
            // Save the cart to DynamoDB
            logger.info("Creating cart: {}", cart);
            DynamoDBUtil.putItem(CARTS_TABLE, cart);
            
            // Return the created cart
            Map<String, Object> cartData = new HashMap<>();
            cartData.put("cart", cart);
            cartData.put("items", new java.util.ArrayList<>());
            
            Map<String, Object> response = ApiResponse.created(cartData);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error creating cart: {}", e.getMessage(), e);
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


















































































