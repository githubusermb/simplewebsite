





































































































































package com.shopcart.handlers.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Order;
import com.shopcart.models.OrderItem;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for updating an order status
 */
public class UpdateOrderStatusHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateOrderStatusHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ORDERS_TABLE = System.getenv("ORDERS_TABLE");
    private static final String ORDER_ITEMS_TABLE = System.getenv("ORDER_ITEMS_TABLE");
    private static final List<String> VALID_STATUSES = Arrays.asList(
        "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");

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
            // Get the order ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("orderId")) {
                logger.error("Order ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Order ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String orderId = pathParameters.get("orderId");
            
            // Parse the request body
            String requestBody = input.getBody();
            if (requestBody == null || requestBody.isEmpty()) {
                logger.error("Request body is empty");
                Map<String, Object> response = ApiResponse.badRequest("Request body is required");
                return convertToApiGatewayResponse(response);
            }
            
            Map<String, String> requestMap = objectMapper.readValue(requestBody, Map.class);
            String status = requestMap.get("status");
            
            if (status == null || status.isEmpty()) {
                logger.error("Status is required");
                Map<String, Object> response = ApiResponse.badRequest("Status is required");
                return convertToApiGatewayResponse(response);
            }
            
            // Validate the status
            if (!VALID_STATUSES.contains(status)) {
                logger.error("Invalid status: {}", status);
                Map<String, Object> response = ApiResponse.badRequest(
                    "Invalid status. Valid statuses are: " + String.join(", ", VALID_STATUSES));
                return convertToApiGatewayResponse(response);
            }
            
            // Get the order from DynamoDB
            logger.info("Getting order with ID: {}", orderId);
            Order order = DynamoDBUtil.getItem(ORDERS_TABLE, "orderId", orderId, Order.class);
            
            if (order == null) {
                logger.error("Order with ID {} not found", orderId);
                Map<String, Object> response = ApiResponse.notFound("Order with ID " + orderId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Update the order status
            order.setStatus(status);
            order.setUpdatedAt(Instant.now().toString());
            
            // Save the updated order to DynamoDB
            logger.info("Updating order status to {}: {}", status, order);
            DynamoDBUtil.updateItem(ORDERS_TABLE, order);
            
            // Get the order items
            logger.info("Getting order items for order ID: {}", orderId);
            List<OrderItem> orderItems = DynamoDBUtil.queryItemsByIndex(
                ORDER_ITEMS_TABLE, "OrderIndex", "orderId", orderId, OrderItem.class);
            
            // Create a response with the updated order and its items
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order", order);
            orderData.put("items", orderItems);
            
            Map<String, Object> response = ApiResponse.success(orderData);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage(), e);
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





































































































































