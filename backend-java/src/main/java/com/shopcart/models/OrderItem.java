




package com.shopcart.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Objects;

/**
 * OrderItem entity model
 */
@DynamoDbBean
public class OrderItem {
    private String orderId;
    private String productId;
    private String name;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private Double totalPrice;
    private String createdAt;

    /**
     * Default constructor required by DynamoDB Enhanced Client
     */
    public OrderItem() {
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
     * Get the product ID
     * @return The product ID
     */
    @DynamoDbSortKey
    public String getProductId() {
        return productId;
    }

    /**
     * Set the product ID
     * @param productId The product ID
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Get the product name
     * @return The product name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the product name
     * @param name The product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the product price
     * @return The product price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Set the product price
     * @param price The product price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Get the quantity
     * @return The quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Set the quantity
     * @param quantity The quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Get the product image URL
     * @return The product image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Set the product image URL
     * @param imageUrl The product image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(orderId, orderItem.orderId) &&
                Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId='" + orderId + '\'' +
                ", productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                ", totalPrice=" + totalPrice +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}




