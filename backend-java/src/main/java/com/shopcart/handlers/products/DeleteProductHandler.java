




































package com.shopcart.handlers.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.shopcart.models.Product;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Lambda function handler for deleting a product
 */
public class DeleteProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteProductHandler.class);
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
            // Get the product ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("productId")) {
                logger.error("Product ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Product ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String productId = pathParameters.get("productId");
            
            // Check if the product exists
            Product existingProduct = DynamoDBUtil.getItem(PRODUCTS_TABLE, "productId", productId, Product.class);
            if (existingProduct == null) {
                logger.error("Product with ID {} not found", productId);
                Map<String, Object> response = ApiResponse.notFound("Product with ID " + productId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Delete the product from DynamoDB
            logger.info("Deleting product with ID: {}", productId);
            DynamoDBUtil.deleteItem(PRODUCTS_TABLE, "productId", productId, Product.class);
            
            // Return a success response
            Map<String, Object> response = ApiResponse.noContent();
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error deleting product: {}", e.getMessage(), e);
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




































