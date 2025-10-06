

const { getItem, scanItems } = require('../utils/dynamodb');
const { success, notFound, serverError } = require('../utils/response');

/**
 * Get all customers or a specific customer by ID
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const customerId = event.pathParameters?.customerId;
    const tableName = process.env.CUSTOMERS_TABLE;

    // If customerId is provided, get a specific customer
    if (customerId) {
      console.log(`Getting customer with ID: ${customerId}`);
      const customer = await getItem(tableName, { customerId });

      if (!customer) {
        return notFound(`Customer with ID ${customerId} not found`);
      }

      return success(customer);
    }

    // Otherwise, get all customers
    console.log('Getting all customers');
    const customers = await scanItems(tableName);
    return success(customers);
  } catch (error) {
    console.error('Error getting customer(s):', error);
    return serverError(error.message);
  }
};

