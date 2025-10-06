

package com.shopcart.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.Objects;

/**
 * Cart entity model
 */
@DynamoDbBean
public class Cart {
    private String cartId;
    private String customerId;
    private Double totalPrice;
    private Integer totalItems;
    private String createdAt;
    private String updatedAt;

    /**
     * Default constructor required by DynamoDB Enhanced Client
     */
    public Cart() {
    }

    /**
     * Get the cart ID
     * @return The cart ID
     */
    @DynamoDbPartitionKey
    public String getCartId() {
        return cartId;
    }

    /**
     * Set the cart ID
     * @param cartId The cart ID
     */
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    /**
     * Get the customer ID
     * @return The customer ID
     */
    @DynamoDbSecondaryPartitionKey(indexNames = {"CustomerIndex"})
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
        Cart cart = (Cart) o;
        return Objects.equals(cartId, cart.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId='" + cartId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", totalPrice=" + totalPrice +
                ", totalItems=" + totalItems +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}

