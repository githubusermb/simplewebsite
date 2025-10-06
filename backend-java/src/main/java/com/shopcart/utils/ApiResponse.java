







package com.shopcart.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for API responses
 */
public class ApiResponse {
    private static final Logger logger = LoggerFactory.getLogger(ApiResponse.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a standardized API response
     * @param statusCode HTTP status code
     * @param body Response body
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> createResponse(int statusCode, Object body, Map<String, String> headers) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        responseHeaders.put("Access-Control-Allow-Origin", "*");
        responseHeaders.put("Access-Control-Allow-Credentials", "true");
        
        if (headers != null) {
            responseHeaders.putAll(headers);
        }
        
        response.put("statusCode", statusCode);
        response.put("headers", responseHeaders);
        
        try {
            if (body instanceof String) {
                response.put("body", body);
            } else {
                response.put("body", objectMapper.writeValueAsString(body));
            }
        } catch (JsonProcessingException e) {
            logger.error("Error serializing response body: {}", e.getMessage());
            response.put("body", "{\"error\": \"Error serializing response\"}");
        }
        
        return response;
    }

    /**
     * Create a success response (200 OK)
     * @param body Response body
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> success(Object body, Map<String, String> headers) {
        return createResponse(200, body, headers);
    }

    /**
     * Create a success response (200 OK)
     * @param body Response body
     * @return API Gateway response object
     */
    public static Map<String, Object> success(Object body) {
        return success(body, null);
    }

    /**
     * Create a created response (201 Created)
     * @param body Response body
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> created(Object body, Map<String, String> headers) {
        return createResponse(201, body, headers);
    }

    /**
     * Create a created response (201 Created)
     * @param body Response body
     * @return API Gateway response object
     */
    public static Map<String, Object> created(Object body) {
        return created(body, null);
    }

    /**
     * Create a no content response (204 No Content)
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> noContent(Map<String, String> headers) {
        return createResponse(204, "", headers);
    }

    /**
     * Create a no content response (204 No Content)
     * @return API Gateway response object
     */
    public static Map<String, Object> noContent() {
        return noContent(null);
    }

    /**
     * Create a bad request response (400 Bad Request)
     * @param message Error message
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> badRequest(String message, Map<String, String> headers) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message != null ? message : "Bad Request");
        return createResponse(400, error, headers);
    }

    /**
     * Create a bad request response (400 Bad Request)
     * @param message Error message
     * @return API Gateway response object
     */
    public static Map<String, Object> badRequest(String message) {
        return badRequest(message, null);
    }

    /**
     * Create an unauthorized response (401 Unauthorized)
     * @param message Error message
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> unauthorized(String message, Map<String, String> headers) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message != null ? message : "Unauthorized");
        return createResponse(401, error, headers);
    }

    /**
     * Create an unauthorized response (401 Unauthorized)
     * @param message Error message
     * @return API Gateway response object
     */
    public static Map<String, Object> unauthorized(String message) {
        return unauthorized(message, null);
    }

    /**
     * Create a forbidden response (403 Forbidden)
     * @param message Error message
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> forbidden(String message, Map<String, String> headers) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message != null ? message : "Forbidden");
        return createResponse(403, error, headers);
    }

    /**
     * Create a forbidden response (403 Forbidden)
     * @param message Error message
     * @return API Gateway response object
     */
    public static Map<String, Object> forbidden(String message) {
        return forbidden(message, null);
    }

    /**
     * Create a not found response (404 Not Found)
     * @param message Error message
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> notFound(String message, Map<String, String> headers) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message != null ? message : "Not Found");
        return createResponse(404, error, headers);
    }

    /**
     * Create a not found response (404 Not Found)
     * @param message Error message
     * @return API Gateway response object
     */
    public static Map<String, Object> notFound(String message) {
        return notFound(message, null);
    }

    /**
     * Create a server error response (500 Internal Server Error)
     * @param message Error message
     * @param headers Additional headers
     * @return API Gateway response object
     */
    public static Map<String, Object> serverError(String message, Map<String, String> headers) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message != null ? message : "Internal Server Error");
        return createResponse(500, error, headers);
    }

    /**
     * Create a server error response (500 Internal Server Error)
     * @param message Error message
     * @return API Gateway response object
     */
    public static Map<String, Object> serverError(String message) {
        return serverError(message, null);
    }
}







