





package com.shopcart.utils;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for DynamoDB operations
 */
public class DynamoDBUtil {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBUtil.class);
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    /**
     * Get an item from DynamoDB
     * @param tableName The DynamoDB table name
     * @param partitionKey The partition key name
     * @param partitionValue The partition key value
     * @param clazz The class type of the item
     * @param <T> The type of the item
     * @return The item from DynamoDB
     */
    public static <T> T getItem(String tableName, String partitionKey, String partitionValue, Class<T> clazz) {
        logger.info("Getting item from table {} with {} = {}", tableName, partitionKey, partitionValue);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            Key key = Key.builder().partitionValue(partitionValue).build();
            return table.getItem(key);
        } catch (Exception e) {
            logger.error("Error getting item from table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Get an item from DynamoDB with a composite key
     * @param tableName The DynamoDB table name
     * @param partitionKey The partition key name
     * @param partitionValue The partition key value
     * @param sortKey The sort key name
     * @param sortValue The sort key value
     * @param clazz The class type of the item
     * @param <T> The type of the item
     * @return The item from DynamoDB
     */
    public static <T> T getItem(String tableName, String partitionKey, String partitionValue, 
                               String sortKey, String sortValue, Class<T> clazz) {
        logger.info("Getting item from table {} with {} = {} and {} = {}", 
                   tableName, partitionKey, partitionValue, sortKey, sortValue);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            Key key = Key.builder()
                    .partitionValue(partitionValue)
                    .sortValue(sortValue)
                    .build();
            return table.getItem(key);
        } catch (Exception e) {
            logger.error("Error getting item from table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Scan all items from DynamoDB
     * @param tableName The DynamoDB table name
     * @param clazz The class type of the items
     * @param <T> The type of the items
     * @return The items from DynamoDB
     */
    public static <T> List<T> scanItems(String tableName, Class<T> clazz) {
        logger.info("Scanning items from table {}", tableName);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            PageIterable<T> pagedResults = table.scan();
            return pagedResults.items().stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error scanning items from table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Query items from DynamoDB by index
     * @param tableName The DynamoDB table name
     * @param indexName The index name
     * @param keyName The key name
     * @param keyValue The key value
     * @param clazz The class type of the items
     * @param <T> The type of the items
     * @return The items from DynamoDB
     */
    public static <T> List<T> queryItemsByIndex(String tableName, String indexName, 
                                              String keyName, String keyValue, Class<T> clazz) {
        logger.info("Querying items from table {} by index {} with {} = {}", 
                   tableName, indexName, keyName, keyValue);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":value", AttributeValue.builder().s(keyValue).build());
            
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(keyValue).build()))
                    .filterExpression(builder -> builder.expression(keyName + " = :value")
                                                      .expressionValues(expressionValues))
                    .build();
            
            PageIterable<T> pagedResults = table.index(indexName).query(request);
            return pagedResults.items().stream().collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error querying items from table {} by index {}: {}", 
                        tableName, indexName, e.getMessage());
            throw e;
        }
    }

    /**
     * Put an item in DynamoDB
     * @param tableName The DynamoDB table name
     * @param item The item to put
     * @param <T> The type of the item
     */
    public static <T> void putItem(String tableName, T item) {
        logger.info("Putting item in table {}: {}", tableName, item);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean((Class<T>) item.getClass()));
            table.putItem(item);
        } catch (Exception e) {
            logger.error("Error putting item in table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Update an item in DynamoDB
     * @param tableName The DynamoDB table name
     * @param item The item to update
     * @param <T> The type of the item
     * @return The updated item
     */
    public static <T> T updateItem(String tableName, T item) {
        logger.info("Updating item in table {}: {}", tableName, item);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean((Class<T>) item.getClass()));
            return table.updateItem(item);
        } catch (Exception e) {
            logger.error("Error updating item in table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Delete an item from DynamoDB
     * @param tableName The DynamoDB table name
     * @param partitionKey The partition key name
     * @param partitionValue The partition key value
     * @param clazz The class type of the item
     * @param <T> The type of the item
     */
    public static <T> void deleteItem(String tableName, String partitionKey, String partitionValue, Class<T> clazz) {
        logger.info("Deleting item from table {} with {} = {}", tableName, partitionKey, partitionValue);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            Key key = Key.builder().partitionValue(partitionValue).build();
            table.deleteItem(key);
        } catch (Exception e) {
            logger.error("Error deleting item from table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Delete an item from DynamoDB with a composite key
     * @param tableName The DynamoDB table name
     * @param partitionKey The partition key name
     * @param partitionValue The partition key value
     * @param sortKey The sort key name
     * @param sortValue The sort key value
     * @param clazz The class type of the item
     * @param <T> The type of the item
     */
    public static <T> void deleteItem(String tableName, String partitionKey, String partitionValue, 
                                    String sortKey, String sortValue, Class<T> clazz) {
        logger.info("Deleting item from table {} with {} = {} and {} = {}", 
                   tableName, partitionKey, partitionValue, sortKey, sortValue);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            Key key = Key.builder()
                    .partitionValue(partitionValue)
                    .sortValue(sortValue)
                    .build();
            table.deleteItem(key);
        } catch (Exception e) {
            logger.error("Error deleting item from table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Batch write items to DynamoDB
     * @param tableName The DynamoDB table name
     * @param items The items to write
     * @param <T> The type of the items
     */
    public static <T> void batchWriteItems(String tableName, List<T> items, Class<T> clazz) {
        logger.info("Batch writing {} items to table {}", items.size(), tableName);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            
            WriteBatch.Builder<T> writeBatchBuilder = WriteBatch.builder(clazz)
                    .mappedTableResource(table);
            
            for (T item : items) {
                writeBatchBuilder.addPutItem(item);
            }
            
            BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(writeBatchBuilder.build())
                    .build();
            
            enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);
        } catch (Exception e) {
            logger.error("Error batch writing items to table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Batch delete items from DynamoDB
     * @param tableName The DynamoDB table name
     * @param keys The keys of the items to delete
     * @param <T> The type of the items
     */
    public static <T> void batchDeleteItems(String tableName, List<Key> keys, Class<T> clazz) {
        logger.info("Batch deleting {} items from table {}", keys.size(), tableName);
        
        try {
            DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(clazz));
            
            WriteBatch.Builder<T> writeBatchBuilder = WriteBatch.builder(clazz)
                    .mappedTableResource(table);
            
            for (Key key : keys) {
                writeBatchBuilder.addDeleteItem(key);
            }
            
            BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(writeBatchBuilder.build())
                    .build();
            
            enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);
        } catch (Exception e) {
            logger.error("Error batch deleting items from table {}: {}", tableName, e.getMessage());
            throw e;
        }
    }
}





