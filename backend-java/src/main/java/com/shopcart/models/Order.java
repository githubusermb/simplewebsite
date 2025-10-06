



package com.shopcart.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

import java.util.Objects;

/**
 * Order entity model
 */
@DynamoDbBean
public class Order {
    private String orderId;
    private String customerId;
    private String orderDate;
    private String status;
    private Double totalPrice;
    private Integer totalItems;
    private String shippingAddress;
    private String paymentMethod;
    private String createdAt;
    private String updatedAt;

    /**
     * Default constructor required by DynamoDB Enhanced Client
     */
    public Order() {
    }

    /**
     * Get the order ID
     * @return The order ID
     */
    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }

    /**
     * Set the order ID
     * @param orderId The order ID
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Get the customer ID
     * @return The customer ID
     */
    @DynamoDbSecondaryPartitionKey(indexNames = {"CustomerOrderIndex"})
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Set the customer ID
     * @param customerId The customer ID
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Get the order date
     * @return The order date
     */
    @DynamoDbSecondarySortKey(indexNames = {"CustomerOrderIndex"})
    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Set the order date
     * @param orderDate The order date
     */
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Get the order status
     * @return The order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the order status
     * @param status The order status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the total price
     * @return The total price
     */
    public Double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Set the total price
     * @param totalPrice The total price
     */
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Get the total items
     * @return The total items
     */
    public Integer getTotalItems() {
        return totalItems;
    }

    /**
     * Set the total items
     * @param totalItems The total items
     */
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Get the shipping address
     * @return The shipping address
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Set the shipping address
     * @param shippingAddress The shipping address
     */
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * Get the payment method
     * @return The payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Set the payment method
     * @param paymentMethod The payment method
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Get the creation timestamp
     * @return The creation timestamp
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the creation timestamp
     * @param createdAt The creation timestamp
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the update timestamp
     * @return The update timestamp
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set the update timestamp
     * @param updatedAt The update timestamp
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", totalItems=" + totalItems +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}



