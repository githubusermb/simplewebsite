












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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Lambda function handler for creating a customer
 */
public class CreateCustomerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CreateCustomerHandler.class);
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
            // Parse the request body
            String requestBody = input.getBody();
            if (requestBody == null || requestBody.isEmpty()) {
                logger.error("Request body is empty");
                Map<String, Object> response = ApiResponse.badRequest("Request body is required");
                return convertToApiGatewayResponse(response);
            }
            
            Customer customer = objectMapper.readValue(requestBody, Customer.class);
            
            // Validate required fields
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                logger.error("Email is required");
                Map<String, Object> response = ApiResponse.badRequest("Email is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                logger.error("Password is required");
                Map<String, Object> response = ApiResponse.badRequest("Password is required");
                return convertToApiGatewayResponse(response);
            }
            
            // Check if email already exists
            try {
                List<Customer> existingCustomers = DynamoDBUtil.queryItemsByIndex(
                    CUSTOMERS_TABLE, "EmailIndex", "email", customer.getEmail(), Customer.class);
                
                if (!existingCustomers.isEmpty()) {
                    logger.error("Email already exists: {}", customer.getEmail());
                    Map<String, Object> response = ApiResponse.badRequest("Email already exists");
                    return convertToApiGatewayResponse(response);
                }
            } catch (Exception e) {
                logger.warn("Error checking for existing email: {}", e.getMessage());
                // Continue with customer creation even if email check fails
            }
            
            // Generate customer ID and timestamps
            String customerId = UUID.randomUUID().toString();
            String timestamp = Instant.now().toString();
            
            customer.setCustomerId(customerId);
            customer.setCreatedAt(timestamp);
            customer.setUpdatedAt(timestamp);
            
            // In a real application, we would hash the password here
            // customer.setPassword(hashPassword(customer.getPassword()));
            
            // Save the customer to DynamoDB
            logger.info("Creating customer: {}", customer);
            DynamoDBUtil.putItem(CUSTOMERS_TABLE, customer);
            
            // Return the created customer
            Map<String, Object> response = ApiResponse.created(customer);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error creating customer: {}", e.getMessage(), e);
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












