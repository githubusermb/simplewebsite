




























package com.shopcart.handlers.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Product;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Lambda function handler for creating a product
 */
public class CreateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CreateProductHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PRODUCTS_TABLE = System.getenv("PRODUCTS_TABLE");

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
            
            Product product = objectMapper.readValue(requestBody, Product.class);
            
            // Validate required fields
            if (product.getName() == null || product.getName().isEmpty()) {
                logger.error("Product name is required");
                Map<String, Object> response = ApiResponse.badRequest("Product name is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (product.getPrice() == null) {
                logger.error("Product price is required");
                Map<String, Object> response = ApiResponse.badRequest("Product price is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (product.getCategoryId() == null || product.getCategoryId().isEmpty()) {
                logger.error("Category ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Category ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            // Generate product ID and timestamps
            String productId = UUID.randomUUID().toString();
            String timestamp = Instant.now().toString();
            
            product.setProductId(productId);
            product.setCreatedAt(timestamp);
            product.setUpdatedAt(timestamp);
            
            // Set default values if not provided
            if (product.getStock() == null) {
                product.setStock(0);
            }
            
            if (product.getDescription() == null) {
                product.setDescription("");
            }
            
            if (product.getImageUrl() == null) {
                product.setImageUrl("");
            }
            
            // Save the product to DynamoDB
            logger.info("Creating product: {}", product);
            DynamoDBUtil.putItem(PRODUCTS_TABLE, product);
            
            // Return the created product
            Map<String, Object> response = ApiResponse.created(product);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error creating product: {}", e.getMessage(), e);
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




























