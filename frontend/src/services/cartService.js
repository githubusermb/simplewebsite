



















import api from './api';

const cartService = {
  // Create a new cart
  createCart: async (customerId) => {
    try {
      const response = await api.post('/carts', { customerId });
      return response.data;
    } catch (error) {
      console.error('Error creating cart:', error);
      throw error;
    }
  },

  // Get cart by ID
  getCart: async (cartId) => {
    try {
      const response = await api.get(`/carts/${cartId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching cart ${cartId}:`, error);
      throw error;
    }
  },

  // Get customer's active cart
  getCustomerCart: async (customerId) => {
    try {
      const response = await api.get(`/customers/${customerId}/cart`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching cart for customer ${customerId}:`, error);
      throw error;
    }
  },

  // Add item to cart
  addItemToCart: async (cartId, productId, quantity) => {
    try {
      const response = await api.post(`/carts/${cartId}/items`, {
        productId,
        quantity
      });
      return response.data;
    } catch (error) {
      console.error(`Error adding item to cart ${cartId}:`, error);
      throw error;
    }
  },

  // Update cart item quantity
  updateCartItem: async (cartId, productId, quantity) => {
    try {
      const response = await api.put(`/carts/${cartId}/items/${productId}`, {
        quantity
      });
      return response.data;
    } catch (error) {
      console.error(`Error updating item in cart ${cartId}:`, error);
      throw error;
    }
  },

  // Remove item from cart
  removeCartItem: async (cartId, productId) => {
    try {
      const response = await api.delete(`/carts/${cartId}/items/${productId}`);
      return response.data;
    } catch (error) {
      console.error(`Error removing item from cart ${cartId}:`, error);
      throw error;
    }
  }
};

export default cartService;



















