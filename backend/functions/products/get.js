




const { getItem, scanItems, queryItems } = require('../utils/dynamodb');
const { success, notFound, serverError } = require('../utils/response');

/**
 * Get all products, products by category, or a specific product by ID
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const productId = event.pathParameters?.productId;
    const categoryId = event.pathParameters?.categoryId;
    const tableName = process.env.PRODUCTS_TABLE;

    // If productId is provided, get a specific product
    if (productId) {
      console.log(`Getting product with ID: ${productId}`);
      const product = await getItem(tableName, { productId });

      if (!product) {
        return notFound(`Product with ID ${productId} not found`);
      }

      return success(product);
    }

    // If categoryId is provided, get products by category
    if (categoryId) {
      console.log(`Getting products for category: ${categoryId}`);
      const products = await queryItems(tableName, {
        IndexName: 'CategoryIndex',
        KeyConditionExpression: 'categoryId = :categoryId',
        ExpressionAttributeValues: {
          ':categoryId': categoryId
        }
      });

      return success(products);
    }

    // Otherwise, get all products
    console.log('Getting all products');
    const products = await scanItems(tableName);
    return success(products);
  } catch (error) {
    console.error('Error getting product(s):', error);
    return serverError(error.message);
  }
};




