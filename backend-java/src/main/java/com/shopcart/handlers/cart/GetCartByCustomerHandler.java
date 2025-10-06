









































































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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for getting a cart by customer ID
 */
public class GetCartByCustomerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(GetCartByCustomerHandler.class);
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
            // Get the customer ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("customerId")) {
                logger.error("Customer ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Customer ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String customerId = pathParameters.get("customerId");
            
            // Get the cart from DynamoDB by customer ID
            logger.info("Getting cart for customer ID: {}", customerId);
            List<Cart> carts = DynamoDBUtil.queryItemsByIndex(
                CARTS_TABLE, "CustomerIndex", "customerId", customerId, Cart.class);
            
            if (carts.isEmpty()) {
                logger.info("No cart found for customer ID {}", customerId);
                Map<String, Object> response = ApiResponse.notFound("No cart found for customer ID " + customerId);
                return convertToApiGatewayResponse(response);
            }
            
            // Get the most recent cart (assuming there's only one active cart per customer)
            Cart cart = carts.get(0);
            
            // Get the cart items
            logger.info("Getting cart items for cart ID: {}", cart.getCartId());
            List<CartItem> cartItems = DynamoDBUtil.queryItemsByIndex(
                CART_ITEMS_TABLE, "CartIndex", "cartId", cart.getCartId(), CartItem.class);
            
            // Create a response with the cart and its items
            Map<String, Object> cartData = new HashMap<>();
            cartData.put("cart", cart);
            cartData.put("items", cartItems);
            
            logger.info("Found cart: {} with {} items", cart, cartItems.size());
            Map<String, Object> response = ApiResponse.success(cartData);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error getting cart by customer ID: {}", e.getMessage(), e);
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









































































