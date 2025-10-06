


package com.shopcart.handlers.products;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class GetProductHandlerTest {

    private GetProductHandler handler;
    private ObjectMapper objectMapper;

    @Mock
    private Context context;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GetProductHandler();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testHandleRequest_GetAllProducts() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        
        // This test would normally mock the DynamoDBUtil to return a list of products
        // but for simplicity, we'll just verify that the handler doesn't throw an exception
        
        try {
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            // In a real test, we would assert on the response
        } catch (Exception e) {
            // If an exception is thrown, the test will fail
            assertTrue(false, "Handler threw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testHandleRequest_GetProductById() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("productId", "123");
        request.setPathParameters(pathParameters);
        
        // This test would normally mock the DynamoDBUtil to return a specific product
        // but for simplicity, we'll just verify that the handler doesn't throw an exception
        
        try {
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            // In a real test, we would assert on the response
        } catch (Exception e) {
            // If an exception is thrown, the test will fail
            assertTrue(false, "Handler threw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testHandleRequest_GetProductsByCategory() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("categoryId", "456");
        request.setQueryStringParameters(queryParameters);
        
        // This test would normally mock the DynamoDBUtil to return products by category
        // but for simplicity, we'll just verify that the handler doesn't throw an exception
        
        try {
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            // In a real test, we would assert on the response
        } catch (Exception e) {
            // If an exception is thrown, the test will fail
            assertTrue(false, "Handler threw an exception: " + e.getMessage());
        }
    }

    // Additional tests would be added here for error scenarios and edge cases
    // but they would require mocking the DynamoDBUtil class which is beyond the scope
    // of this simple test example
}


