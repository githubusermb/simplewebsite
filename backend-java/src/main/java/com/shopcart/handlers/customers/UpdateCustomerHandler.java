















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
import java.util.Map;

/**
 * Lambda function handler for updating a customer
 */
public class UpdateCustomerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomerHandler.class);
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
            // Get the customer ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("customerId")) {
                logger.error("Customer ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Customer ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String customerId = pathParameters.get("customerId");
            
            // Parse the request body
            String requestBody = input.getBody();
            if (requestBody == null || requestBody.isEmpty()) {
                logger.error("Request body is empty");
                Map<String, Object> response = ApiResponse.badRequest("Request body is required");
                return convertToApiGatewayResponse(response);
            }
            
            Customer updatedCustomer = objectMapper.readValue(requestBody, Customer.class);
            
            // Get the existing customer
            Customer existingCustomer = DynamoDBUtil.getItem(CUSTOMERS_TABLE, "customerId", customerId, Customer.class);
            if (existingCustomer == null) {
                logger.error("Customer with ID {} not found", customerId);
                Map<String, Object> response = ApiResponse.notFound("Customer with ID " + customerId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Update the customer fields
            if (updatedCustomer.getFirstName() != null) {
                existingCustomer.setFirstName(updatedCustomer.getFirstName());
            }
            
            if (updatedCustomer.getLastName() != null) {
                existingCustomer.setLastName(updatedCustomer.getLastName());
            }
            
            if (updatedCustomer.getAddress() != null) {
                existingCustomer.setAddress(updatedCustomer.getAddress());
            }
            
            if (updatedCustomer.getPhone() != null) {
                existingCustomer.setPhone(updatedCustomer.getPhone());
            }
            
            // Email and password updates require special handling
            if (updatedCustomer.getEmail() != null && !updatedCustomer.getEmail().equals(existingCustomer.getEmail())) {
                // Check if the new email already exists
                try {
                    java.util.List<Customer> existingCustomers = DynamoDBUtil.queryItemsByIndex(
                        CUSTOMERS_TABLE, "EmailIndex", "email", updatedCustomer.getEmail(), Customer.class);
                    
                    if (!existingCustomers.isEmpty()) {
                        logger.error("Email already exists: {}", updatedCustomer.getEmail());
                        Map<String, Object> response = ApiResponse.badRequest("Email already exists");
                        return convertToApiGatewayResponse(response);
                    }
                } catch (Exception e) {
                    logger.warn("Error checking for existing email: {}", e.getMessage());
                    // Continue with customer update even if email check fails
                }
                
                existingCustomer.setEmail(updatedCustomer.getEmail());
            }
            
            if (updatedCustomer.getPassword() != null && !updatedCustomer.getPassword().isEmpty()) {
                // In a real application, we would hash the password here
                // existingCustomer.setPassword(hashPassword(updatedCustomer.getPassword()));
                existingCustomer.setPassword(updatedCustomer.getPassword());
            }
            
            // Update the timestamp
            existingCustomer.setUpdatedAt(Instant.now().toString());
            
            // Save the updated customer to DynamoDB
            logger.info("Updating customer: {}", existingCustomer);
            DynamoDBUtil.updateItem(CUSTOMERS_TABLE, existingCustomer);
            
            // Return the updated customer
            Map<String, Object> response = ApiResponse.success(existingCustomer);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error updating customer: {}", e.getMessage(), e);
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















