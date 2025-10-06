

package com.shopcart.handlers.customers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class CreateCustomerHandlerTest {

    private CreateCustomerHandler handler;
    private ObjectMapper objectMapper;

    @Mock
    private Context context;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new CreateCustomerHandler();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testHandleRequest_MissingRequestBody() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Request body is required"));
    }

    @Test
    public void testHandleRequest_MissingEmail() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setPassword("password123");
        
        request.setBody(objectMapper.writeValueAsString(customer));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Email is required"));
    }

    @Test
    public void testHandleRequest_MissingPassword() throws Exception {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        
        request.setBody(objectMapper.writeValueAsString(customer));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Password is required"));
    }

    // Additional tests would be added here for successful creation and other scenarios
    // but they would require mocking the DynamoDBUtil class which is beyond the scope
    // of this simple test example
}

