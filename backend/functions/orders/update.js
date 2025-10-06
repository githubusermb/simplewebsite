





























































const { getItem, updateItem } = require('../utils/dynamodb');
const { success, notFound, badRequest, serverError } = require('../utils/response');

/**
 * Update an order's status
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const orderId = event.pathParameters.orderId;
    const ordersTable = process.env.ORDERS_TABLE;
    const body = JSON.parse(event.body);

    // Check if order exists
    const existingOrder = await getItem(ordersTable, { orderId });
    if (!existingOrder) {
      return notFound(`Order with ID ${orderId} not found`);
    }

    // Validate status
    if (!body.status) {
      return badRequest('Missing required field: status');
    }

    // Validate status value
    const validStatuses = ['pending', 'processing', 'shipped', 'delivered', 'cancelled'];
    if (!validStatuses.includes(body.status)) {
      return badRequest(`Invalid status. Must be one of: ${validStatuses.join(', ')}`);
    }

    const timestamp = new Date().toISOString();

    // Update order status
    const updatedOrder = await updateItem(
      ordersTable,
      { orderId },
      'SET #status = :status, updatedAt = :updatedAt',
      {
        '#status': 'status'
      },
      {
        ':status': body.status,
        ':updatedAt': timestamp
      }
    );

    return success(updatedOrder);
  } catch (error) {
    console.error('Error updating order:', error);
    return serverError(error.message);
  }
};





























































