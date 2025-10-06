












const { getItem, scanItems } = require('../utils/dynamodb');
const { success, notFound, serverError } = require('../utils/response');

/**
 * Get all categories or a specific category by ID
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const categoryId = event.pathParameters?.categoryId;
    const tableName = process.env.CATEGORIES_TABLE;

    // If categoryId is provided, get a specific category
    if (categoryId) {
      console.log(`Getting category with ID: ${categoryId}`);
      const category = await getItem(tableName, { categoryId });

      if (!category) {
        return notFound(`Category with ID ${categoryId} not found`);
      }

      return success(category);
    }

    // Otherwise, get all categories
    console.log('Getting all categories');
    const categories = await scanItems(tableName);
    return success(categories);
  } catch (error) {
    console.error('Error getting category(s):', error);
    return serverError(error.message);
  }
};












