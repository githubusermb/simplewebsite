






















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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for customer login
 */
public class LoginHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
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
            
            // Extract email and password from the request
            Map<String, String> credentials = objectMapper.readValue(requestBody, Map.class);
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            if (email == null || email.isEmpty()) {
                logger.error("Email is required");
                Map<String, Object> response = ApiResponse.badRequest("Email is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (password == null || password.isEmpty()) {
                logger.error("Password is required");
                Map<String, Object> response = ApiResponse.badRequest("Password is required");
                return convertToApiGatewayResponse(response);
            }
            
            // Find the customer by email
            List<Customer> customers;
            try {
                customers = DynamoDBUtil.queryItemsByIndex(
                    CUSTOMERS_TABLE, "EmailIndex", "email", email, Customer.class);
            } catch (Exception e) {
                logger.error("Error querying customer by email: {}", e.getMessage(), e);
                Map<String, Object> response = ApiResponse.serverError("Error querying customer by email");
                return convertToApiGatewayResponse(response);
            }
            
            if (customers.isEmpty()) {
                logger.error("Customer with email {} not found", email);
                Map<String, Object> response = ApiResponse.unauthorized("Invalid email or password");
                return convertToApiGatewayResponse(response);
            }
            
            Customer customer = customers.get(0);
            
            // In a real application, we would verify the password hash here
            // if (!verifyPassword(password, customer.getPassword())) {
            if (!password.equals(customer.getPassword())) {
                logger.error("Invalid password for customer with email {}", email);
                Map<String, Object> response = ApiResponse.unauthorized("Invalid email or password");
                return convertToApiGatewayResponse(response);
            }
            
            // Create a response with the customer data (excluding the password)
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("customerId", customer.getCustomerId());
            customerData.put("email", customer.getEmail());
            customerData.put("firstName", customer.getFirstName());
            customerData.put("lastName", customer.getLastName());
            customerData.put("address", customer.getAddress());
            customerData.put("phone", customer.getPhone());
            
            // In a real application, we would generate a JWT token here
            // String token = generateToken(customer);
            // customerData.put("token", token);
            
            Map<String, Object> response = ApiResponse.success(customerData);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage(), e);
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






















