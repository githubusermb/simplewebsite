













































package com.shopcart.handlers.categories;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Category;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Lambda function handler for creating a category
 */
public class CreateCategoryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CreateCategoryHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CATEGORIES_TABLE = System.getenv("CATEGORIES_TABLE");

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
            
            Category category = objectMapper.readValue(requestBody, Category.class);
            
            // Validate required fields
            if (category.getName() == null || category.getName().isEmpty()) {
                logger.error("Category name is required");
                Map<String, Object> response = ApiResponse.badRequest("Category name is required");
                return convertToApiGatewayResponse(response);
            }
            
            // Generate category ID and timestamps
            String categoryId = UUID.randomUUID().toString();
            String timestamp = Instant.now().toString();
            
            category.setCategoryId(categoryId);
            category.setCreatedAt(timestamp);
            category.setUpdatedAt(timestamp);
            
            // Set default values if not provided
            if (category.getDescription() == null) {
                category.setDescription("");
            }
            
            if (category.getImageUrl() == null) {
                category.setImageUrl("");
            }
            
            // Save the category to DynamoDB
            logger.info("Creating category: {}", category);
            DynamoDBUtil.putItem(CATEGORIES_TABLE, category);
            
            // Return the created category
            Map<String, Object> response = ApiResponse.created(category);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
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













































