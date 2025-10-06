























































































































package com.shopcart.handlers.orders;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.models.*;
import com.shopcart.utils.ApiResponse;
import com.shopcart.utils.DynamoDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

/**
 * Lambda function handler for creating an order
 */
public class CreateOrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ORDERS_TABLE = System.getenv("ORDERS_TABLE");
    private static final String ORDER_ITEMS_TABLE = System.getenv("ORDER_ITEMS_TABLE");
    private static final String CARTS_TABLE = System.getenv("CARTS_TABLE");
    private static final String CART_ITEMS_TABLE = System.getenv("CART_ITEMS_TABLE");
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
            
            Map<String, String> requestMap = objectMapper.readValue(requestBody, Map.class);
            String cartId = requestMap.get("cartId");
            String customerId = requestMap.get("customerId");
            String shippingAddress = requestMap.get("shippingAddress");
            String paymentMethod = requestMap.get("paymentMethod");
            
            if (cartId == null || cartId.isEmpty()) {
                logger.error("Cart ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Cart ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (customerId == null || customerId.isEmpty()) {
                logger.error("Customer ID is required");
                Map<String, Object> response = ApiResponse.badRequest("Customer ID is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (shippingAddress == null || shippingAddress.isEmpty()) {
                logger.error("Shipping address is required");
                Map<String, Object> response = ApiResponse.badRequest("Shipping address is required");
                return convertToApiGatewayResponse(response);
            }
            
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                logger.error("Payment method is required");
                Map<String, Object> response = ApiResponse.badRequest("Payment method is required");
                return convertToApiGatewayResponse(response);
            }
            
            // Get the cart from DynamoDB
            logger.info("Getting cart with ID: {}", cartId);
            Cart cart = DynamoDBUtil.getItem(CARTS_TABLE, "cartId", cartId, Cart.class);
            
            if (cart == null) {
                logger.error("Cart with ID {} not found", cartId);
                Map<String, Object> response = ApiResponse.notFound("Cart with ID " + cartId + " not found");
                return convertToApiGatewayResponse(response);
            }
            
            // Check if the cart belongs to the customer
            if (!cart.getCustomerId().equals(customerId)) {
                logger.error("Cart with ID {} does not belong to customer {}", cartId, customerId);
                Map<String, Object> response = ApiResponse.forbidden("Cart does not belong to the customer");
                return convertToApiGatewayResponse(response);
            }
            
            // Check if the cart is empty
            if (cart.getTotalItems() == 0) {
                logger.error("Cart with ID {} is empty", cartId);
                Map<String, Object> response = ApiResponse.badRequest("Cart is empty");
                return convertToApiGatewayResponse(response);
            }
            
            // Get the cart items
            logger.info("Getting cart items for cart ID: {}", cartId);
            List<CartItem> cartItems = DynamoDBUtil.queryItemsByIndex(
                CART_ITEMS_TABLE, "CartIndex", "cartId", cartId, CartItem.class);
            
            if (cartItems.isEmpty()) {
                logger.error("No items found in cart with ID {}", cartId);
                Map<String, Object> response = ApiResponse.badRequest("No items found in cart");
                return convertToApiGatewayResponse(response);
            }
            
            // Create a new order
            String orderId = UUID.randomUUID().toString();
            String timestamp = Instant.now().toString();
            
            Order order = new Order();
            order.setOrderId(orderId);
            order.setCustomerId(customerId);
            order.setTotalPrice(cart.getTotalPrice());
            order.setTotalItems(cart.getTotalItems());
            order.setStatus("PENDING");
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);
            order.setCreatedAt(timestamp);
            order.setUpdatedAt(timestamp);
            
            // Save the order to DynamoDB
            logger.info("Creating order: {}", order);
            DynamoDBUtil.putItem(ORDERS_TABLE, order);
            
            // Create order items from cart items
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setName(cartItem.getName());
                orderItem.setPrice(cartItem.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setImageUrl(cartItem.getImageUrl());
                orderItem.setTotalPrice(cartItem.getTotalPrice());
                orderItem.setCreatedAt(timestamp);
                orderItem.setUpdatedAt(timestamp);
                
                orderItems.add(orderItem);
                
                // Update product stock
                Product product = DynamoDBUtil.getItem(PRODUCTS_TABLE, "productId", cartItem.getProductId(), Product.class);
                if (product != null) {
                    int newStock = product.getStock() - cartItem.getQuantity();
                    if (newStock < 0) newStock = 0;
                    product.setStock(newStock);
                    product.setUpdatedAt(timestamp);
                    DynamoDBUtil.updateItem(PRODUCTS_TABLE, product);
                }
            }
            
            // Save the order items to DynamoDB
            logger.info("Creating {} order items", orderItems.size());
            for (OrderItem orderItem : orderItems) {
                DynamoDBUtil.putItem(ORDER_ITEMS_TABLE, orderItem);
            }
            
            // Clear the cart
            logger.info("Clearing cart with ID: {}", cartId);
            
            // Delete all cart items
            for (CartItem cartItem : cartItems) {
                DynamoDBUtil.deleteItem(
                    CART_ITEMS_TABLE, "cartId", cartId, "productId", cartItem.getProductId(), CartItem.class);
            }
            
            // Update the cart totals
            cart.setTotalPrice(0.0);
            cart.setTotalItems(0);
            cart.setUpdatedAt(timestamp);
            DynamoDBUtil.updateItem(CARTS_TABLE, cart);
            
            // Return the created order and its items
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("order", order);
            orderData.put("items", orderItems);
            
            Map<String, Object> response = ApiResponse.created(orderData);
            return convertToApiGatewayResponse(response);
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
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























































































































