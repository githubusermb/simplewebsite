

























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

import java.util.List;
import java.util.Map;

/**
 * Lambda function handler for getting products
 */
public class GetProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(GetProductHandler.class);
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
            Map<String, String> pathParameters = input.getPathParameters();
            String productId = pathParameters != null ? pathParameters.get("productId") : null;
            
            // If productId is provided, get a specific product
            if (productId != null && !productId.isEmpty()) {
                logger.info("Getting product with ID: {}", productId);
                Product product = DynamoDBUtil.getItem(PRODUCTS_TABLE, "productId", productId, Product.class);
                
                if (product == null) {
                    logger.info("Product with ID {} not found", productId);
                    Map<String, Object> response = ApiResponse.notFound("Product with ID " + productId + " not found");
                    return convertToApiGatewayResponse(response);
                }
                
                logger.info("Found product: {}", product);
                Map<String, Object> response = ApiResponse.success(product);
                return convertToApiGatewayResponse(response);
            }
            
            // Check if we need to filter by category
            Map<String, String> queryParameters = input.getQueryStringParameters();
            String categoryId = queryParameters != null ? queryParameters.get("categoryId") : null;
            
            if (categoryId != null && !categoryId.isEmpty()) {
                // Get products by category
                logger.info("Getting products by category ID: {}", categoryId);
                List<Product> products = DynamoDBUtil.queryItemsByIndex(
                    PRODUCTS_TABLE, "CategoryIndex", "categoryId", categoryId, Product.class);
                
                logger.info("Found {} products in category {}", products.size(), categoryId);
                Map<String, Object> response = ApiResponse.success(products);
                return convertToApiGatewayResponse(response);
            }
            
            // Otherwise, get all products
            logger.info("Getting all products");
            List<Product> products = DynamoDBUtil.scanItems(PRODUCTS_TABLE, Product.class);
            logger.info("Found {} products", products.size());
            
            Map<String, Object> response = ApiResponse.success(products);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error getting product(s): {}", e.getMessage(), e);
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

























