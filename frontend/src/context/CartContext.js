






import React, { createContext, useState, useEffect, useContext } from 'react';
import { AuthContext } from './AuthContext';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState(null);
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const { currentUser } = useContext(AuthContext);

  // Load cart from local storage on component mount or when user changes
  useEffect(() => {
    const loadCart = async () => {
      setLoading(true);
      try {
        if (currentUser) {
          // In a real application, this would be an API call to get the user's cart
          // For now, we'll check local storage
          const storedCart = localStorage.getItem(`cart_${currentUser.customerId}`);
          if (storedCart) {
            const parsedCart = JSON.parse(storedCart);
            setCart(parsedCart);
            setCartItems(parsedCart.items || []);
          } else {
            // Create a new cart for the user
            const newCart = {
              cartId: 'cart_' + Math.random().toString(36).substr(2, 9),
              customerId: currentUser.customerId,
              status: 'active',
              totalItems: 0,
              totalPrice: 0,
              items: []
            };
            localStorage.setItem(`cart_${currentUser.customerId}`, JSON.stringify(newCart));
            setCart(newCart);
            setCartItems([]);
          }
        } else {
          // For anonymous users, use a session cart
          const sessionCart = localStorage.getItem('session_cart');
          if (sessionCart) {
            const parsedCart = JSON.parse(sessionCart);
            setCart(parsedCart);
            setCartItems(parsedCart.items || []);
          } else {
            // Create a new session cart
            const newCart = {
              cartId: 'session_' + Math.random().toString(36).substr(2, 9),
              status: 'active',
              totalItems: 0,
              totalPrice: 0,
              items: []
            };
            localStorage.setItem('session_cart', JSON.stringify(newCart));
            setCart(newCart);
            setCartItems([]);
          }
        }
      } catch (error) {
        console.error('Error loading cart:', error);
        setError('Failed to load cart');
      } finally {
        setLoading(false);
      }
    };

    loadCart();
  }, [currentUser]);

  // Save cart to local storage whenever it changes
  useEffect(() => {
    if (cart) {
      if (currentUser) {
        localStorage.setItem(`cart_${currentUser.customerId}`, JSON.stringify(cart));
      } else {
        localStorage.setItem('session_cart', JSON.stringify(cart));
      }
    }
  }, [cart, currentUser]);

  // Add item to cart
  const addToCart = async (product, quantity = 1) => {
    setLoading(true);
    setError(null);
    
    try {
      // Check if product already exists in cart
      const existingItemIndex = cartItems.findIndex(item => item.productId === product.productId);
      
      let updatedItems;
      if (existingItemIndex >= 0) {
        // Update existing item quantity
        updatedItems = [...cartItems];
        updatedItems[existingItemIndex] = {
          ...updatedItems[existingItemIndex],
          quantity: updatedItems[existingItemIndex].quantity + quantity
        };
      } else {
        // Add new item to cart
        const newItem = {
          productId: product.productId,
          name: product.name,
          price: product.price,
          quantity,
          imageUrl: product.imageUrl
        };
        updatedItems = [...cartItems, newItem];
      }
      
      // Calculate new totals
      const totalItems = updatedItems.reduce((sum, item) => sum + item.quantity, 0);
      const totalPrice = updatedItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      
      // Update cart
      const updatedCart = {
        ...cart,
        items: updatedItems,
        totalItems,
        totalPrice
      };
      
      setCart(updatedCart);
      setCartItems(updatedItems);
      
      return updatedCart;
    } catch (error) {
      setError(error.message || 'Failed to add item to cart');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Update cart item quantity
  const updateCartItem = async (productId, quantity) => {
    setLoading(true);
    setError(null);
    
    try {
      // Find the item in the cart
      const existingItemIndex = cartItems.findIndex(item => item.productId === productId);
      
      if (existingItemIndex < 0) {
        throw new Error('Item not found in cart');
      }
      
      let updatedItems;
      if (quantity <= 0) {
        // Remove item if quantity is 0 or negative
        updatedItems = cartItems.filter(item => item.productId !== productId);
      } else {
        // Update item quantity
        updatedItems = [...cartItems];
        updatedItems[existingItemIndex] = {
          ...updatedItems[existingItemIndex],
          quantity
        };
      }
      
      // Calculate new totals
      const totalItems = updatedItems.reduce((sum, item) => sum + item.quantity, 0);
      const totalPrice = updatedItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      
      // Update cart
      const updatedCart = {
        ...cart,
        items: updatedItems,
        totalItems,
        totalPrice
      };
      
      setCart(updatedCart);
      setCartItems(updatedItems);
      
      return updatedCart;
    } catch (error) {
      setError(error.message || 'Failed to update cart item');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Remove item from cart
  const removeFromCart = async (productId) => {
    return updateCartItem(productId, 0);
  };

  // Clear cart
  const clearCart = async () => {
    setLoading(true);
    setError(null);
    
    try {
      // Create empty cart
      const emptyCart = {
        ...cart,
        items: [],
        totalItems: 0,
        totalPrice: 0
      };
      
      setCart(emptyCart);
      setCartItems([]);
      
      return emptyCart;
    } catch (error) {
      setError(error.message || 'Failed to clear cart');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const value = {
    cart,
    cartItems,
    loading,
    error,
    addToCart,
    updateCartItem,
    removeFromCart,
    clearCart
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};






