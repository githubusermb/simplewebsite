



















































const { v4: uuidv4 } = require('uuid');
const { getItem, putItem, queryItems, updateItem, batchWriteItems } = require('../utils/dynamodb');
const { created, badRequest, notFound, serverError } = require('../utils/response');

/**
 * Create a new order from a cart
 * @param {object} event - API Gateway Lambda Proxy Input Format
 * @returns {object} - API Gateway Lambda Proxy Output Format
 */
exports.handler = async (event) => {
  try {
    const ordersTable = process.env.ORDERS_TABLE;
    const orderItemsTable = process.env.ORDER_ITEMS_TABLE;
    const cartsTable = process.env.CARTS_TABLE;
    const cartItemsTable = process.env.CART_ITEMS_TABLE;
    const body = JSON.parse(event.body);

    // Validate required fields
    if (!body.cartId || !body.customerId || !body.shippingAddress || !body.paymentMethod) {
      return badRequest('Missing required fields: cartId, customerId, shippingAddress, paymentMethod');
    }

    // Check if cart exists
    const cart = await getItem(cartsTable, { cartId: body.cartId });
    if (!cart) {
      return notFound(`Cart with ID ${body.cartId} not found`);
    }

    // Check if cart belongs to customer
    if (cart.customerId !== body.customerId) {
      return badRequest(`Cart ${body.cartId} does not belong to customer ${body.customerId}`);
    }

    // Check if cart is active
    if (cart.status !== 'active') {
      return badRequest(`Cart with ID ${body.cartId} is not active`);
    }

    // Get cart items
    const cartItems = await queryItems(cartItemsTable, {
      KeyConditionExpression: 'cartId = :cartId',
      ExpressionAttributeValues: {
        ':cartId': body.cartId
      }
    });

    if (!cartItems || cartItems.length === 0) {
      return badRequest(`Cart ${body.cartId} is empty`);
    }

    // Create new order
    const timestamp = new Date().toISOString();
    const orderId = uuidv4();
    const order = {
      orderId,
      customerId: body.customerId,
      orderDate: timestamp,
      status: 'pending',
      shippingAddress: body.shippingAddress,
      billingAddress: body.billingAddress || body.shippingAddress,
      paymentMethod: body.paymentMethod,
      totalAmount: cart.totalPrice,
      tax: parseFloat((cart.totalPrice * 0.1).toFixed(2)), // 10% tax
      shippingCost: 10.00, // Fixed shipping cost
      createdAt: timestamp,
      updatedAt: timestamp
    };

    // Calculate final total
    order.totalAmount = parseFloat((order.totalAmount + order.tax + order.shippingCost).toFixed(2));

    // Create order items
    const orderItems = cartItems.map(item => ({
      orderId,
      productId: item.productId,
      quantity: item.quantity,
      price: item.price
    }));

    // Save order
    await putItem(ordersTable, order);

    // Save order items
    await batchWriteItems(orderItemsTable, orderItems);

    // Update cart status to 'converted'
    await updateItem(
      cartsTable,
      { cartId: body.cartId },
      'SET #status = :status, updatedAt = :updatedAt',
      {
        '#status': 'status'
      },
      {
        ':status': 'converted',
        ':updatedAt': timestamp
      }
    );

    // Add items to order response
    order.items = orderItems;

    return created(order);
  } catch (error) {
    console.error('Error creating order:', error);
    return serverError(error.message);
  }
};



















































