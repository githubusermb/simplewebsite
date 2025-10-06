





const { v4: uuidv4 } = require('uuid');
const { putItem, getItem } = require('../utils/dynamodb');
const { created, badRequest, notFound, serverError } = require('../utils/response');

/**
 * Create a new product
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const tableName = process.env.PRODUCTS_TABLE;
    const body = JSON.parse(event.body);

    // Validate required fields
    if (!body.name || !body.price || !body.categoryId) {
      return badRequest('Missing required fields: name, price, categoryId');
    }

    // Validate price is a number
    if (isNaN(parseFloat(body.price))) {
      return badRequest('Price must be a number');
    }

    // Check if category exists
    // Note: In a real application, you would check if the category exists
    // This is just a placeholder for demonstration purposes
    /*
    const categoriesTable = process.env.CATEGORIES_TABLE;
    const category = await getItem(categoriesTable, { categoryId: body.categoryId });
    if (!category) {
      return notFound(`Category with ID ${body.categoryId} not found`);
    }
    */

    // Create new product
    const timestamp = new Date().toISOString();
    const product = {
      productId: uuidv4(),
      name: body.name,
      description: body.description || '',
      price: parseFloat(body.price),
      categoryId: body.categoryId,
      imageUrl: body.imageUrl || '',
      inventory: body.inventory || 0,
      createdAt: timestamp,
      updatedAt: timestamp
    };

    await putItem(tableName, product);
    return created(product);
  } catch (error) {
    console.error('Error creating product:', error);
    return serverError(error.message);
  }
};





