















const { v4: uuidv4 } = require('uuid');
const { putItem } = require('../utils/dynamodb');
const { created, badRequest, serverError } = require('../utils/response');

/**
 * Create a new category
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const tableName = process.env.CATEGORIES_TABLE;
    const body = JSON.parse(event.body);

    // Validate required fields
    if (!body.name) {
      return badRequest('Missing required field: name');
    }

    // Create new category
    const timestamp = new Date().toISOString();
    const category = {
      categoryId: uuidv4(),
      name: body.name,
      description: body.description || '',
      imageUrl: body.imageUrl || '',
      createdAt: timestamp,
      updatedAt: timestamp
    };

    await putItem(tableName, category);
    return created(category);
  } catch (error) {
    console.error('Error creating category:', error);
    return serverError(error.message);
  }
};















