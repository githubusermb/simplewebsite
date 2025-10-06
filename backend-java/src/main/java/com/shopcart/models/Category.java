

package com.shopcart.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

/**
 * Category entity model
 */
@DynamoDbBean
public class Category {
    private String categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private String createdAt;
    private String updatedAt;

    /**
     * Default constructor required by DynamoDB Enhanced Client
     */
    public Category() {
    }

    /**
     * Get the category ID
     * @return The category ID
     */
    @DynamoDbPartitionKey
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
     * Get the category name
     * @return The category name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the category name
     * @param name The category name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the category description
     * @return The category description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the category description
     * @param description The category description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the category image URL
     * @return The category image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Set the category image URL
     * @param imageUrl The category image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
        Category category = (Category) o;
        return Objects.equals(categoryId, category.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId='" + categoryId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}

