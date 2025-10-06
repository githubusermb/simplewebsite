































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

/**
 * Lambda function handler for updating a product
 */
public class UpdateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateProductHandler.class);
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
            // Get the product ID from the path parameters
            Map<String, String> pathParameters = input.getPathParameters();
            if (pathParameters == null || !pathParameters.containsKey("productId")) {
                logger.error("Product ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Product ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            String productId = pathParameters.get("productId");
            
            // Parse the request body
            String requestBody = input.getBody();
            if (requestBody == null || requestBody.isEmpty()) {
                logger.error("Request body is empty");
                Map<String, Object> response = ApiResponse.badRequest("Request body is required");
                return convertToApiGatewayResponse(response);
            }
            
            Product updatedProduct = objectMapper.readValue(requestBody, Product.class);
            
            // Get the existing product
            Product existingProduct = DynamoDBUtil.getItem(PRODUCTS_TABLE, "productId", productId, Product.class);
            if (existingProduct == null) {
                logger.error("Product with ID {} not found", productId);
                Map<String, Object> response = ApiResponse.notFound("Product with ID " + productId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Update the product fields
            if (updatedProduct.getName() != null) {
                existingProduct.setName(updatedProduct.getName());
            }
            
            if (updatedProduct.getDescription() != null) {
                existingProduct.setDescription(updatedProduct.getDescription());
            }
            
            if (updatedProduct.getPrice() != null) {
                existingProduct.setPrice(updatedProduct.getPrice());
            }
            
            if (updatedProduct.getStock() != null) {
                existingProduct.setStock(updatedProduct.getStock());
            }
            
            if (updatedProduct.getImageUrl() != null) {
                existingProduct.setImageUrl(updatedProduct.getImageUrl());
            }
            
            if (updatedProduct.getCategoryId() != null) {
                existingProduct.setCategoryId(updatedProduct.getCategoryId());
            }
            
            // Update the timestamp
            existingProduct.setUpdatedAt(Instant.now().toString());
            
            // Save the updated product to DynamoDB
            logger.info("Updating product: {}", existingProduct);
            DynamoDBUtil.updateItem(PRODUCTS_TABLE, existingProduct);
            
            // Return the updated product
            Map<String, Object> response = ApiResponse.success(existingProduct);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error updating product: {}", e.getMessage(), e);
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































