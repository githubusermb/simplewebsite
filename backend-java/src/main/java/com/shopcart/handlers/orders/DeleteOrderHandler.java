
package com.shopcart.handlers.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.shopcart.models.Order;
import com.shopcart.models.OrderItem;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lambda function handler for deleting an order
 */
public class DeleteOrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteOrderHandler.class);
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
            // Get the order ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("orderId")) {
                logger.error("Order ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Order ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String orderId = pathParameters.get("orderId");
            
            // Get the order from DynamoDB
            logger.info("Getting order with ID: {}", orderId);
            Order order = DynamoDBUtil.getItem(ORDERS_TABLE, "orderId", orderId, Order.class);
            
            if (order == null) {
                logger.error("Order with ID {} not found", orderId);
                Map<String, Object> response = ApiResponse.notFound("Order with ID " + orderId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Check if the order can be deleted (only PENDING orders can be deleted)
            if (!"PENDING".equals(order.getStatus())) {
                logger.error("Cannot delete order with status {}", order.getStatus());
                Map<String, Object> response = ApiResponse.badRequest(
                    "Cannot delete order with status " + order.getStatus() + ". Only PENDING orders can be deleted.");
                return convertToApiGatewayResponse(response);
            }
            
            // Get all order items
            logger.info("Getting all order items for order ID: {}", orderId);
            List<OrderItem> orderItems = DynamoDBUtil.queryItemsByIndex(
                ORDER_ITEMS_TABLE, "OrderIndex", "orderId", orderId, OrderItem.class);
            
            if (!orderItems.isEmpty()) {
                // Delete all order items
                logger.info("Deleting {} order items", orderItems.size());
                List<Key> keys = orderItems.stream()
                    .map(item -> Key.builder()
                        .partitionValue(item.getOrderId())
                        .sortValue(item.getProductId())
                        .build())
                    .collect(Collectors.toList());
                
                DynamoDBUtil.batchDeleteItems(ORDER_ITEMS_TABLE, keys, OrderItem.class);
            }
            
            // Delete the order from DynamoDB
            logger.info("Deleting order with ID: {}", orderId);
            DynamoDBUtil.deleteItem(ORDERS_TABLE, "orderId", orderId, Order.class);
            
            // Return a success response
            Map<String, Object> response = ApiResponse.noContent();
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error deleting order: {}", e.getMessage(), e);
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
