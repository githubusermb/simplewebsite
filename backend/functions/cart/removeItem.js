






































const { getItem, deleteItem, updateItem } = require('../utils/dynamodb');
const { success, notFound, badRequest, serverError } = require('../utils/response');

/**
 * Remove an item from a cart
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const cartId = event.pathParameters.cartId;
    const productId = event.pathParameters.productId;
    const cartsTable = process.env.CARTS_TABLE;
    const cartItemsTable = process.env.CART_ITEMS_TABLE;

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
    const quantityChange = -existingItem.quantity;
    const priceChange = -existingItem.price * existingItem.quantity;

    // Delete item from cart
    await deleteItem(cartItemsTable, { cartId, productId });

    // Update cart totals
    const updatedCart = await updateItem(
      cartsTable,
      { cartId },
      'SET totalItems = totalItems + :quantityChange, totalPrice = totalPrice + :priceChange, updatedAt = :updatedAt',
      {},
      {
        ':quantityChange': quantityChange,
        ':priceChange': priceChange,
        ':updatedAt': timestamp
      }
    );

    return success({
      message: 'Item removed from cart successfully',
      cart: updatedCart
    });
  } catch (error) {
    console.error('Error removing item from cart:', error);
    return serverError(error.message);
  }
};






































