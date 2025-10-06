



const { getItem, deleteItem } = require('../utils/dynamodb');
const { noContent, notFound, serverError } = require('../utils/response');

/**
 * Delete a customer
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const customerId = event.pathParameters.customerId;
    const tableName = process.env.CUSTOMERS_TABLE;

    // Check if customer exists
    const existingCustomer = await getItem(tableName, { customerId });
    if (!existingCustomer) {
      return notFound(`Customer with ID ${customerId} not found`);
    }

    // Delete customer
    await deleteItem(tableName, { customerId });
    return noContent();
  } catch (error) {
    console.error('Error deleting customer:', error);
    return serverError(error.message);
  }
};



