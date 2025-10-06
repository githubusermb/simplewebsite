









const { getItem, deleteItem } = require('../utils/dynamodb');
const { noContent, notFound, serverError } = require('../utils/response');

/**
 * Delete a product
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const productId = event.pathParameters.productId;
    const tableName = process.env.PRODUCTS_TABLE;

    // Check if product exists
    const existingProduct = await getItem(tableName, { productId });
    if (!existingProduct) {
      return notFound(`Product with ID ${productId} not found`);
    }

    // Delete product
    await deleteItem(tableName, { productId });
    return noContent();
  } catch (error) {
    console.error('Error deleting product:', error);
    return serverError(error.message);
  }
};









