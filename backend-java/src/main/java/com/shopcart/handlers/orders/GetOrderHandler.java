






























































































































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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for getting an order
 */
public class GetOrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(GetOrderHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ORDERS_TABLE = System.getenv("ORDERS_TABLE");
    private static final String ORDER_ITEMS_TABLE = System.getenv("ORDER_ITEMS_TABLE");

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
            Map<String, String> pathParameters = input.getPathParameters();
            String orderId = pathParameters != null ? pathParameters.get("orderId") : null;
            
            // If orderId is provided, get a specific order
            if (orderId != null && !orderId.isEmpty()) {
                logger.info("Getting order with ID: {}", orderId);
                Order order = DynamoDBUtil.getItem(ORDERS_TABLE, "orderId", orderId, Order.class);
                
                if (order == null) {
                    logger.info("Order with ID {} not found", orderId);
                    Map<String, Object> response = ApiResponse.notFound("Order with ID " + orderId + " not found");
                    return convertToApiGatewayResponse(response);
                }
                
                // Get the order items
                logger.info("Getting order items for order ID: {}", orderId);
                List<OrderItem> orderItems = DynamoDBUtil.queryItemsByIndex(
                    ORDER_ITEMS_TABLE, "OrderIndex", "orderId", orderId, OrderItem.class);
                
                // Create a response with the order and its items
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("order", order);
                orderData.put("items", orderItems);
                
                logger.info("Found order: {} with {} items", order, orderItems.size());
                Map<String, Object> response = ApiResponse.success(orderData);
                return convertToApiGatewayResponse(response);
            }
            
            // Check if we need to filter by customer
            Map<String, String> queryParameters = input.getQueryStringParameters();
            String customerId = queryParameters != null ? queryParameters.get("customerId") : null;
            
            if (customerId != null && !customerId.isEmpty()) {
                // Get orders by customer
                logger.info("Getting orders by customer ID: {}", customerId);
                List<Order> orders = DynamoDBUtil.queryItemsByIndex(
                    ORDERS_TABLE, "CustomerIndex", "customerId", customerId, Order.class);
                
                logger.info("Found {} orders for customer {}", orders.size(), customerId);
                Map<String, Object> response = ApiResponse.success(orders);
                return convertToApiGatewayResponse(response);
            }
            
            // Otherwise, get all orders (admin only)
            logger.info("Getting all orders");
            List<Order> orders = DynamoDBUtil.scanItems(ORDERS_TABLE, Order.class);
            logger.info("Found {} orders", orders.size());
            
            Map<String, Object> response = ApiResponse.success(orders);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error getting order(s): {}", e.getMessage(), e);
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






























































































































