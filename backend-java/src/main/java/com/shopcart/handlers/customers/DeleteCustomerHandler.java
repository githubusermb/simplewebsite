


















package com.shopcart.handlers.customers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.shopcart.models.Customer;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Lambda function handler for deleting a customer
 */
public class DeleteCustomerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteCustomerHandler.class);
    private static final String CUSTOMERS_TABLE = System.getenv("CUSTOMERS_TABLE");

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
            
            // Check if the customer exists
            Customer existingCustomer = DynamoDBUtil.getItem(CUSTOMERS_TABLE, "customerId", customerId, Customer.class);
            if (existingCustomer == null) {
                logger.error("Customer with ID {} not found", customerId);
                Map<String, Object> response = ApiResponse.notFound("Customer with ID " + customerId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Delete the customer from DynamoDB
            logger.info("Deleting customer with ID: {}", customerId);
            DynamoDBUtil.deleteItem(CUSTOMERS_TABLE, "customerId", customerId, Customer.class);
            
            // Return a success response
            Map<String, Object> response = ApiResponse.noContent();
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error deleting customer: {}", e.getMessage(), e);
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


















