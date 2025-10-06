





















const { getItem, deleteItem } = require('../utils/dynamodb');
const { noContent, notFound, serverError } = require('../utils/response');

/**
 * Delete a category
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const categoryId = event.pathParameters.categoryId;
    const tableName = process.env.CATEGORIES_TABLE;

    // Check if category exists
    const existingCategory = await getItem(tableName, { categoryId });
    if (!existingCategory) {
      return notFound(`Category with ID ${categoryId} not found`);
    }

    // Delete category
    await deleteItem(tableName, { categoryId });
    return noContent();
  } catch (error) {
    console.error('Error deleting category:', error);
    return serverError(error.message);
  }
};





















