
const AWS = require('aws-sdk');
const dynamodb = new AWS.DynamoDB.DocumentClient();

/**
 * Get an item from DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {object} key - The key of the item to get
 * @returns {Promise<object>} - The item from DynamoDB
 */
const getItem = async (tableName, key) => {
  const params = {
    TableName: tableName,
    Key: key
  };

  try {
    const result = await dynamodb.get(params).promise();
    return result.Item;
  } catch (error) {
    console.error(`Error getting item from ${tableName}:`, error);
    throw error;
  }
};

/**
 * Query items from DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {object} params - The query parameters
 * @returns {Promise<Array>} - The items from DynamoDB
 */
const queryItems = async (tableName, params) => {
  const queryParams = {
    TableName: tableName,
    ...params
  };

  try {
    const result = await dynamodb.query(queryParams).promise();
    return result.Items;
  } catch (error) {
    console.error(`Error querying items from ${tableName}:`, error);
    throw error;
  }
};

/**
 * Scan items from DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {object} params - The scan parameters
 * @returns {Promise<Array>} - The items from DynamoDB
 */
const scanItems = async (tableName, params = {}) => {
  const scanParams = {
    TableName: tableName,
    ...params
  };

  try {
    const result = await dynamodb.scan(scanParams).promise();
    return result.Items;
  } catch (error) {
    console.error(`Error scanning items from ${tableName}:`, error);
    throw error;
  }
};

/**
 * Put an item in DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {object} item - The item to put
 * @returns {Promise<object>} - The result from DynamoDB
 */
const putItem = async (tableName, item) => {
  const params = {
    TableName: tableName,
    Item: item
  };

  try {
    return await dynamodb.put(params).promise();
  } catch (error) {
    console.error(`Error putting item in ${tableName}:`, error);
    throw error;
  }
};

/**
 * Update an item in DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {object} key - The key of the item to update
 * @param {object} updateExpression - The update expression
 * @param {object} expressionAttributeNames - The expression attribute names
 * @param {object} expressionAttributeValues - The expression attribute values
 * @returns {Promise<object>} - The updated item from DynamoDB
 */
const updateItem = async (
  tableName,
  key,
  updateExpression,
  expressionAttributeNames,
  expressionAttributeValues
) => {
  const params = {
    TableName: tableName,
    Key: key,
    UpdateExpression: updateExpression,
    ExpressionAttributeNames: expressionAttributeNames,
    ExpressionAttributeValues: expressionAttributeValues,
    ReturnValues: 'ALL_NEW'
  };

  try {
    const result = await dynamodb.update(params).promise();
    return result.Attributes;
  } catch (error) {
    console.error(`Error updating item in ${tableName}:`, error);
    throw error;
  }
};

/**
 * Delete an item from DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {object} key - The key of the item to delete
 * @returns {Promise<object>} - The result from DynamoDB
 */
const deleteItem = async (tableName, key) => {
  const params = {
    TableName: tableName,
    Key: key
  };

  try {
    return await dynamodb.delete(params).promise();
  } catch (error) {
    console.error(`Error deleting item from ${tableName}:`, error);
    throw error;
  }
};

/**
 * Batch write items to DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {Array} items - The items to write
 * @returns {Promise<object>} - The result from DynamoDB
 */
const batchWriteItems = async (tableName, items) => {
  const params = {
    RequestItems: {
      [tableName]: items.map(item => ({
        PutRequest: {
          Item: item
        }
      }))
    }
  };

  try {
    return await dynamodb.batchWrite(params).promise();
  } catch (error) {
    console.error(`Error batch writing items to ${tableName}:`, error);
    throw error;
  }
};

/**
 * Batch delete items from DynamoDB
 * @param {string} tableName - The DynamoDB table name
 * @param {Array} keys - The keys of the items to delete
 * @returns {Promise<object>} - The result from DynamoDB
 */
const batchDeleteItems = async (tableName, keys) => {
  const params = {
    RequestItems: {
      [tableName]: keys.map(key => ({
        DeleteRequest: {
          Key: key
        }
      }))
    }
  };

  try {
    return await dynamodb.batchWrite(params).promise();
  } catch (error) {
    console.error(`Error batch deleting items from ${tableName}:`, error);
    throw error;
  }
};

module.exports = {
  getItem,
  queryItems,
  scanItems,
  putItem,
  updateItem,
  deleteItem,
  batchWriteItems,
  batchDeleteItems
};
