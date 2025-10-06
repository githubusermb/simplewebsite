












































const { getItem, scanItems, queryItems } = require('../utils/dynamodb');
const { success, notFound, serverError } = require('../utils/response');

/**
 * Get all orders, orders by customer, or a specific order by ID
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const orderId = event.pathParameters?.orderId;
    const customerId = event.pathParameters?.customerId;
    const ordersTable = process.env.ORDERS_TABLE;
    const orderItemsTable = process.env.ORDER_ITEMS_TABLE;

    // If orderId is provided, get a specific order
    if (orderId) {
      console.log(`Getting order with ID: ${orderId}`);
      const order = await getItem(ordersTable, { orderId });

      if (!order) {
        return notFound(`Order with ID ${orderId} not found`);
      }

      // Get order items
      const orderItems = await queryItems(orderItemsTable, {
        KeyConditionExpression: 'orderId = :orderId',
        ExpressionAttributeValues: {
          ':orderId': orderId
        }
      });

      // Add items to order
      order.items = orderItems || [];

      return success(order);
    }

    // If customerId is provided, get customer's orders
    if (customerId) {
      console.log(`Getting orders for customer: ${customerId}`);
      const orders = await queryItems(ordersTable, {
        IndexName: 'CustomerOrderIndex',
        KeyConditionExpression: 'customerId = :customerId',
        ExpressionAttributeValues: {
          ':customerId': customerId
        }
      });

      // For each order, get its items
      for (const order of orders) {
        const orderItems = await queryItems(orderItemsTable, {
          KeyConditionExpression: 'orderId = :orderId',
          ExpressionAttributeValues: {
            ':orderId': order.orderId
          }
        });

        order.items = orderItems || [];
      }

      return success(orders);
    }

    // Otherwise, get all orders (admin only)
    console.log('Getting all orders');
    const orders = await scanItems(ordersTable);
    return success(orders);
  } catch (error) {
    console.error('Error getting order(s):', error);
    return serverError(error.message);
  }
};












































