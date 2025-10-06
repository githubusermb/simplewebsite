


const { getItem, updateItem } = require('../utils/dynamodb');
const { success, notFound, badRequest, serverError } = require('../utils/response');

/**
 * Update an existing customer
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const customerId = event.pathParameters.customerId;
    const tableName = process.env.CUSTOMERS_TABLE;
    const body = JSON.parse(event.body);

    // Check if customer exists
    const existingCustomer = await getItem(tableName, { customerId });
    if (!existingCustomer) {
      return notFound(`Customer with ID ${customerId} not found`);
    }

    // Build update expression
    let updateExpression = 'SET updatedAt = :updatedAt';
    const expressionAttributeNames = {};
    const expressionAttributeValues = {
      ':updatedAt': new Date().toISOString()
    };

    // Add fields to update expression
    const allowedFields = ['firstName', 'lastName', 'email', 'address', 'phoneNumber'];
    
    let hasUpdates = false;
    for (const field of allowedFields) {
      if (body[field] !== undefined) {
        updateExpression += `, #${field} = :${field}`;
        expressionAttributeNames[`#${field}`] = field;
        expressionAttributeValues[`:${field}`] = body[field];
        hasUpdates = true;
      }
    }

    if (!hasUpdates) {
      return badRequest('No valid fields to update');
    }

    // Update customer
    const updatedCustomer = await updateItem(
      tableName,
      { customerId },
      updateExpression,
      expressionAttributeNames,
      expressionAttributeValues
    );

    return success(updatedCustomer);
  } catch (error) {
    console.error('Error updating customer:', error);
    return serverError(error.message);
  }
};


