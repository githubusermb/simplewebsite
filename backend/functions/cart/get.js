

























const { getItem, queryItems } = require('../utils/dynamodb');
const { success, notFound, serverError } = require('../utils/response');

/**
 * Get a cart by ID or customer ID
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const cartId = event.pathParameters?.cartId;
    const customerId = event.pathParameters?.customerId;
    const cartsTable = process.env.CARTS_TABLE;
    const cartItemsTable = process.env.CART_ITEMS_TABLE;

    // If cartId is provided, get a specific cart
    if (cartId) {
      console.log(`Getting cart with ID: ${cartId}`);
      const cart = await getItem(cartsTable, { cartId });

      if (!cart) {
        return notFound(`Cart with ID ${cartId} not found`);
      }

      // Get cart items
      const cartItems = await queryItems(cartItemsTable, {
        KeyConditionExpression: 'cartId = :cartId',
        ExpressionAttributeValues: {
          ':cartId': cartId
        }
      });

      // Add items to cart
      cart.items = cartItems || [];

      return success(cart);
    }

    // If customerId is provided, get customer's active cart
    if (customerId) {
      console.log(`Getting cart for customer: ${customerId}`);
      const carts = await queryItems(cartsTable, {
        IndexName: 'CustomerIndex',
        KeyConditionExpression: 'customerId = :customerId',
        FilterExpression: '#status = :status',
        ExpressionAttributeNames: {
          '#status': 'status'
        },
        ExpressionAttributeValues: {
          ':customerId': customerId,
          ':status': 'active'
        }
      });

      if (!carts || carts.length === 0) {
        return notFound(`No active cart found for customer ${customerId}`);
      }

      const cart = carts[0]; // Get the first active cart

      // Get cart items
      const cartItems = await queryItems(cartItemsTable, {
        KeyConditionExpression: 'cartId = :cartId',
        ExpressionAttributeValues: {
          ':cartId': cart.cartId
        }
      });

      // Add items to cart
      cart.items = cartItems || [];

      return success(cart);
    }

    return notFound('Cart ID or Customer ID is required');
  } catch (error) {
    console.error('Error getting cart:', error);
    return serverError(error.message);
  }
};

























