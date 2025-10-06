


const { v4: uuidv4 } = require('uuid');
const { putItem, queryItems } = require('../utils/dynamodb');
const { created, badRequest, serverError } = require('../utils/response');

/**
 * Create a new customer
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const tableName = process.env.CUSTOMERS_TABLE;
    const body = JSON.parse(event.body);

    // Validate required fields
    if (!body.email || !body.firstName || !body.lastName) {
      return badRequest('Missing required fields: email, firstName, lastName');
    }

    // Check if email already exists
    const existingCustomers = await queryItems(tableName, {
      IndexName: 'EmailIndex',
      KeyConditionExpression: 'email = :email',
      ExpressionAttributeValues: {
        ':email': body.email
      }
    });

    if (existingCustomers && existingCustomers.length > 0) {
      return badRequest(`Customer with email ${body.email} already exists`);
    }

    // Create new customer
    const timestamp = new Date().toISOString();
    const customer = {
      customerId: uuidv4(),
      email: body.email,
      firstName: body.firstName,
      lastName: body.lastName,
      address: body.address || {},
      phoneNumber: body.phoneNumber || '',
      createdAt: timestamp,
      updatedAt: timestamp
    };

    await putItem(tableName, customer);
    return created(customer);
  } catch (error) {
    console.error('Error creating customer:', error);
    return serverError(error.message);
  }
};


