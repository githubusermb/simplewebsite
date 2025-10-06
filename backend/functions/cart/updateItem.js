



































const { getItem, updateItem } = require('../utils/dynamodb');
const { success, notFound, badRequest, serverError } = require('../utils/response');

/**
 * Update an item in a cart
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const cartId = event.pathParameters.cartId;
    const productId = event.pathParameters.productId;
    const cartsTable = process.env.CARTS_TABLE;
    const cartItemsTable = process.env.CART_ITEMS_TABLE;
    const body = JSON.parse(event.body);

    // Validate required fields
    if (!body.quantity) {
      return badRequest('Missing required field: quantity');
    }

    // Validate quantity is a positive number
    const quantity = parseInt(body.quantity);
    if (isNaN(quantity) || quantity <= 0) {
      return badRequest('Quantity must be a positive number');
    }

    // Check if cart exists
    const cart = await getItem(cartsTable, { cartId });
    if (!cart) {
      return notFound(`Cart with ID ${cartId} not found`);
    }

    // Check if cart is active
    if (cart.status !== 'active') {
      return badRequest(`Cart with ID ${cartId} is not active`);
    }

    // Check if item exists in cart
    const existingItem = await getItem(cartItemsTable, { cartId, productId });
    if (!existingItem) {
      return notFound(`Item with product ID ${productId} not found in cart ${cartId}`);
    }

    const timestamp = new Date().toISOString();
    const quantityDifference = quantity - existingItem.quantity;
    const priceDifference = existingItem.price * quantityDifference;

    // Update item quantity
    await updateItem(
      cartItemsTable,
      { cartId, productId },
      'SET #quantity = :quantity, #updatedAt = :updatedAt',
      {
        '#quantity': 'quantity',
        '#updatedAt': 'updatedAt'
      },
      {
        ':quantity': quantity,
        ':updatedAt': timestamp
      }
    );

    // Update cart totals
    const updatedCart = await updateItem(
      cartsTable,
      { cartId },
      'SET totalItems = totalItems + :quantityChange, totalPrice = totalPrice + :priceChange, updatedAt = :updatedAt',
      {},
      {
        ':quantityChange': quantityDifference,
        ':priceChange': priceDifference,
        ':updatedAt': timestamp
      }
    );

    return success({
      message: 'Cart item updated successfully',
      cart: updatedCart
    });
  } catch (error) {
    console.error('Error updating cart item:', error);
    return serverError(error.message);
  }
};



































