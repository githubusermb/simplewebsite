







const { getItem, updateItem } = require('../utils/dynamodb');
const { success, notFound, badRequest, serverError } = require('../utils/response');

/**
 * Update an existing product
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const productId = event.pathParameters.productId;
    const tableName = process.env.PRODUCTS_TABLE;
    const body = JSON.parse(event.body);

    // Check if product exists
    const existingProduct = await getItem(tableName, { productId });
    if (!existingProduct) {
      return notFound(`Product with ID ${productId} not found`);
    }

    // Build update expression
    let updateExpression = 'SET updatedAt = :updatedAt';
    const expressionAttributeNames = {};
    const expressionAttributeValues = {
      ':updatedAt': new Date().toISOString()
    };

    // Add fields to update expression
    const allowedFields = ['name', 'description', 'price', 'categoryId', 'imageUrl', 'inventory'];
    
    let hasUpdates = false;
    for (const field of allowedFields) {
      if (body[field] !== undefined) {
        // Validate price is a number
        if (field === 'price' && isNaN(parseFloat(body[field]))) {
          return badRequest('Price must be a number');
        }
        
        // Validate inventory is a number
        if (field === 'inventory' && isNaN(parseInt(body[field]))) {
          return badRequest('Inventory must be a number');
        }
        
        updateExpression += `, #${field} = :${field}`;
        expressionAttributeNames[`#${field}`] = field;
        
        // Convert price and inventory to numbers
        if (field === 'price') {
          expressionAttributeValues[`:${field}`] = parseFloat(body[field]);
        } else if (field === 'inventory') {
          expressionAttributeValues[`:${field}`] = parseInt(body[field]);
        } else {
          expressionAttributeValues[`:${field}`] = body[field];
        }
        
        hasUpdates = true;
      }
    }

    if (!hasUpdates) {
      return badRequest('No valid fields to update');
    }

    // Update product
    const updatedProduct = await updateItem(
      tableName,
      { productId },
      updateExpression,
      expressionAttributeNames,
      expressionAttributeValues
    );

    return success(updatedProduct);
  } catch (error) {
    console.error('Error updating product:', error);
    return serverError(error.message);
  }
};







