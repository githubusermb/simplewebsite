


















const { getItem, updateItem } = require('../utils/dynamodb');
const { success, notFound, badRequest, serverError } = require('../utils/response');

/**
 * Update an existing category
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const categoryId = event.pathParameters.categoryId;
    const tableName = process.env.CATEGORIES_TABLE;
    const body = JSON.parse(event.body);

    // Check if category exists
    const existingCategory = await getItem(tableName, { categoryId });
    if (!existingCategory) {
      return notFound(`Category with ID ${categoryId} not found`);
    }

    // Build update expression
    let updateExpression = 'SET updatedAt = :updatedAt';
    const expressionAttributeNames = {};
    const expressionAttributeValues = {
      ':updatedAt': new Date().toISOString()
    };

    // Add fields to update expression
    const allowedFields = ['name', 'description', 'imageUrl'];
    
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

    // Update category
    const updatedCategory = await updateItem(
      tableName,
      { categoryId },
      updateExpression,
      expressionAttributeNames,
      expressionAttributeValues
    );

    return success(updatedCategory);
  } catch (error) {
    console.error('Error updating category:', error);
    return serverError(error.message);
  }
};


















