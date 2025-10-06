
package com.shopcart.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.Objects;

/**
 * Customer entity model
 */
@DynamoDbBean
public class Customer {
    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    private String password; // In a real application, this would be hashed
    private String address;
    private String phone;
    private String createdAt;
    private String updatedAt;

    /**
     * Default constructor required by DynamoDB Enhanced Client
     */
    public Customer() {
    }

    /**
     * Get the customer ID
     * @return The customer ID
     */
    @DynamoDbPartitionKey
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
     * Get the customer email
     * @return The customer email
     */
    @DynamoDbSecondaryPartitionKey(indexNames = {"EmailIndex"})
    public String getEmail() {
        return email;
    }

    /**
     * Set the customer email
     * @param email The customer email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the customer first name
     * @return The customer first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the customer first name
     * @param firstName The customer first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the customer last name
     * @return The customer last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the customer last name
     * @param lastName The customer last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the customer password
     * @return The customer password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the customer password
     * @param password The customer password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the customer address
     * @return The customer address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the customer address
     * @param address The customer address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the customer phone
     * @return The customer phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the customer phone
     * @param phone The customer phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
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
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) &&
                Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, email);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
