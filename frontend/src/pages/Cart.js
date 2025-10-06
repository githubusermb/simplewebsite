





































































































































import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { CartContext } from '../context/CartContext';
import { AuthContext } from '../context/AuthContext';
import CartItem from '../components/CartItem';
import './Cart.css';

const Cart = () => {
  const { cart, cartItems, clearCart, loading } = useContext(CartContext);
  const { currentUser } = useContext(AuthContext);
  const navigate = useNavigate();
  
  const handleClearCart = () => {
    if (window.confirm('Are you sure you want to clear your cart?')) {
      clearCart();
    }
  };
  
  const handleCheckout = () => {
    if (currentUser) {
      navigate('/checkout');
    } else {
      navigate('/login', { state: { from: '/checkout' } });
    }
  };
  
  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading cart...</p>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <h1>Your Shopping Cart</h1>
      
      {cartItems.length > 0 ? (
        <>
          <div className="cart-header">
            <div className="cart-header-item product">Product</div>
            <div className="cart-header-item price">Price</div>
            <div className="cart-header-item quantity">Quantity</div>
            <div className="cart-header-item subtotal">Subtotal</div>
            <div className="cart-header-item actions">Actions</div>
          </div>
          
          <div className="cart-items">
            {cartItems.map(item => (
              <CartItem key={item.productId} item={item} />
            ))}
          </div>
          
          <div className="cart-summary">
            <div className="cart-actions">
              <button 
                className="btn btn-outline"
                onClick={handleClearCart}
              >
                Clear Cart
              </button>
              <Link to="/products" className="btn btn-outline">
                Continue Shopping
              </Link>
            </div>
            
            <div className="cart-totals">
              <div className="cart-total-row">
                <span>Subtotal:</span>
                <span>${cart.totalPrice.toFixed(2)}</span>
              </div>
              <div className="cart-total-row">
                <span>Shipping:</span>
                <span>Free</span>
              </div>
              <div className="cart-total-row total">
                <span>Total:</span>
                <span>${cart.totalPrice.toFixed(2)}</span>
              </div>
              
              <button 
                className="btn btn-primary checkout-btn"
                onClick={handleCheckout}
              >
                Proceed to Checkout
              </button>
            </div>
          </div>
        </>
      ) : (
        <div className="empty-cart">
          <div className="empty-cart-icon">
            <i className="fas fa-shopping-cart"></i>
          </div>
          <h2>Your cart is empty</h2>
          <p>Looks like you haven't added any products to your cart yet.</p>
          <Link to="/products" className="btn btn-primary">
            Start Shopping
          </Link>
        </div>
      )}
    </div>
  );
};

export default Cart;





































































































































