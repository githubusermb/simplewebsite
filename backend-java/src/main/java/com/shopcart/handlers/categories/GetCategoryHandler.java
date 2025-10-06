








































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

import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for getting categories
 */
public class GetCategoryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(GetCategoryHandler.class);
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
            Map<String, String> pathParameters = input.getPathParameters();
            String categoryId = pathParameters != null ? pathParameters.get("categoryId") : null;
            
            // If categoryId is provided, get a specific category
            if (categoryId != null && !categoryId.isEmpty()) {
                logger.info("Getting category with ID: {}", categoryId);
                Category category = DynamoDBUtil.getItem(CATEGORIES_TABLE, "categoryId", categoryId, Category.class);
                
                if (category == null) {
                    logger.info("Category with ID {} not found", categoryId);
                    Map<String, Object> response = ApiResponse.notFound("Category with ID " + categoryId + " not found");
                    return convertToApiGatewayResponse(response);
                }
                
                logger.info("Found category: {}", category);
                Map<String, Object> response = ApiResponse.success(category);
                return convertToApiGatewayResponse(response);
            }
            
            // Otherwise, get all categories
            logger.info("Getting all categories");
            List<Category> categories = DynamoDBUtil.scanItems(CATEGORIES_TABLE, Category.class);
            logger.info("Found {} categories", categories.size());
            
            Map<String, Object> response = ApiResponse.success(categories);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error getting category(ies): {}", e.getMessage(), e);
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








































