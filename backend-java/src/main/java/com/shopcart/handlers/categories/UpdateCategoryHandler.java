


















































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

/**
 * Lambda function handler for updating a category
 */
public class UpdateCategoryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCategoryHandler.class);
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
            // Get the category ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("categoryId")) {
                logger.error("Category ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Category ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String categoryId = pathParameters.get("categoryId");
            
            // Parse the request body
            String requestBody = input.getBody();
            if (requestBody == null || requestBody.isEmpty()) {
                logger.error("Request body is empty");
                Map<String, Object> response = ApiResponse.badRequest("Request body is required");
                return convertToApiGatewayResponse(response);
            }
            
            Category updatedCategory = objectMapper.readValue(requestBody, Category.class);
            
            // Get the existing category
            Category existingCategory = DynamoDBUtil.getItem(CATEGORIES_TABLE, "categoryId", categoryId, Category.class);
            if (existingCategory == null) {
                logger.error("Category with ID {} not found", categoryId);
                Map<String, Object> response = ApiResponse.notFound("Category with ID " + categoryId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Update the category fields
            if (updatedCategory.getName() != null) {
                existingCategory.setName(updatedCategory.getName());
            }
            
            if (updatedCategory.getDescription() != null) {
                existingCategory.setDescription(updatedCategory.getDescription());
            }
            
            if (updatedCategory.getImageUrl() != null) {
                existingCategory.setImageUrl(updatedCategory.getImageUrl());
            }
            
            // Update the timestamp
            existingCategory.setUpdatedAt(Instant.now().toString());
            
            // Save the updated category to DynamoDB
            logger.info("Updating category: {}", existingCategory);
            DynamoDBUtil.updateItem(CATEGORIES_TABLE, existingCategory);
            
            // Return the updated category
            Map<String, Object> response = ApiResponse.success(existingCategory);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage(), e);
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


















































