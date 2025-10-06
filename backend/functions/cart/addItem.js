






























const { getItem, putItem, updateItem } = require('../utils/dynamodb');
const { success, notFound, badRequest, serverError } = require('../utils/response');

/**
 * Add an item to a cart
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const cartId = event.pathParameters.cartId;
    const cartsTable = process.env.CARTS_TABLE;
    const cartItemsTable = process.env.CART_ITEMS_TABLE;
    const productsTable = process.env.PRODUCTS_TABLE;
    const body = JSON.parse(event.body);

    // Validate required fields
    if (!body.productId || !body.quantity) {
      return badRequest('Missing required fields: productId, quantity');
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

    // Check if product exists
    const product = await getItem(productsTable, { productId: body.productId });
    if (!product) {
      return notFound(`Product with ID ${body.productId} not found`);
    }

    // Check if product is in stock
    if (product.inventory < quantity) {
      return badRequest(`Not enough inventory for product ${body.productId}`);
    }

    // Check if item already exists in cart
    const existingItem = await getItem(cartItemsTable, { 
      cartId, 
      productId: body.productId 
    });

    const timestamp = new Date().toISOString();
    let newQuantity = quantity;
    let priceChange = product.price * quantity;

    if (existingItem) {
      // Update existing item
      newQuantity = existingItem.quantity + quantity;
      priceChange = product.price * (newQuantity - existingItem.quantity);

      await updateItem(
        cartItemsTable,
        { cartId, productId: body.productId },
        'SET #quantity = :quantity, #price = :price, #updatedAt = :updatedAt',
        {
          '#quantity': 'quantity',
          '#price': 'price',
          '#updatedAt': 'updatedAt'
        },
        {
          ':quantity': newQuantity,
          ':price': product.price,
          ':updatedAt': timestamp
        }
      );
    } else {
      // Add new item
      const cartItem = {
        cartId,
        productId: body.productId,
        quantity,
        price: product.price,
        addedAt: timestamp
      };

      await putItem(cartItemsTable, cartItem);
    }

    // Update cart totals
    const updatedCart = await updateItem(
      cartsTable,
      { cartId },
      'SET totalItems = totalItems + :quantityChange, totalPrice = totalPrice + :priceChange, updatedAt = :updatedAt',
      {},
      {
        ':quantityChange': existingItem ? quantity : quantity,
        ':priceChange': priceChange,
        ':updatedAt': timestamp
      }
    );

    return success({
      message: 'Item added to cart successfully',
      cart: updatedCart
    });
  } catch (error) {
    console.error('Error adding item to cart:', error);
    return serverError(error.message);
  }
};






























