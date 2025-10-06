
package com.shopcart.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.Objects;

/**
 * Product entity model
 */
@DynamoDbBean
public class Product {
    private String productId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String categoryId;
    private String createdAt;
    private String updatedAt;

    /**
     * Default constructor required by DynamoDB Enhanced Client
     */
    public Product() {
    }

    /**
     * Get the product ID
     * @return The product ID
     */
    @DynamoDbPartitionKey
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
     * Get the product description
     * @return The product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the product description
     * @param description The product description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Get the product stock
     * @return The product stock
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Set the product stock
     * @param stock The product stock
     */
    public void setStock(Integer stock) {
        this.stock = stock;
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
     * Get the category ID
     * @return The category ID
     */
    @DynamoDbSecondaryPartitionKey(indexNames = {"CategoryIndex"})
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Set the category ID
     * @param categoryId The category ID
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", imageUrl='" + imageUrl + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
