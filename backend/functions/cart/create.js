




























const { v4: uuidv4 } = require('uuid');
const { putItem, queryItems } = require('../utils/dynamodb');
const { created, badRequest, serverError } = require('../utils/response');

/**
 * Create a new cart
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const tableName = process.env.CARTS_TABLE;
    const body = JSON.parse(event.body);

    // Validate required fields
    if (!body.customerId) {
      return badRequest('Missing required field: customerId');
    }

    // Check if customer already has an active cart
    const existingCarts = await queryItems(tableName, {
      IndexName: 'CustomerIndex',
      KeyConditionExpression: 'customerId = :customerId',
      FilterExpression: '#status = :status',
      ExpressionAttributeNames: {
        '#status': 'status'
      },
      ExpressionAttributeValues: {
        ':customerId': body.customerId,
        ':status': 'active'
      }
    });

    if (existingCarts && existingCarts.length > 0) {
      return badRequest(`Customer ${body.customerId} already has an active cart`);
    }

    // Create new cart
    const timestamp = new Date().toISOString();
    const cart = {
      cartId: uuidv4(),
      customerId: body.customerId,
      status: 'active',
      totalItems: 0,
      totalPrice: 0,
      createdAt: timestamp,
      updatedAt: timestamp
    };

    await putItem(tableName, cart);
    return created(cart);
  } catch (error) {
    console.error('Error creating cart:', error);
    return serverError(error.message);
  }
};




























