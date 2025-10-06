










package com.shopcart.handlers.customers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Customer;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for getting customers
 */
public class GetCustomerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(GetCustomerHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
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
            Map<String, String> pathParameters = input.getPathParameters();
            String customerId = pathParameters != null ? pathParameters.get("customerId") : null;
            
            // If customerId is provided, get a specific customer
            if (customerId != null && !customerId.isEmpty()) {
                logger.info("Getting customer with ID: {}", customerId);
                Customer customer = DynamoDBUtil.getItem(CUSTOMERS_TABLE, "customerId", customerId, Customer.class);
                
                if (customer == null) {
                    logger.info("Customer with ID {} not found", customerId);
                    Map<String, Object> response = ApiResponse.notFound("Customer with ID " + customerId + " not found");
                    return convertToApiGatewayResponse(response);
                }
                
                logger.info("Found customer: {}", customer);
                Map<String, Object> response = ApiResponse.success(customer);
                return convertToApiGatewayResponse(response);
            }
            
            // Otherwise, get all customers
            logger.info("Getting all customers");
            List<Customer> customers = DynamoDBUtil.scanItems(CUSTOMERS_TABLE, Customer.class);
            logger.info("Found {} customers", customers.size());
            
            Map<String, Object> response = ApiResponse.success(customers);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error getting customer(s): {}", e.getMessage(), e);
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










