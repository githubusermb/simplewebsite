






























































































































































import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { CartContext } from '../context/CartContext';
import { AuthContext } from '../context/AuthContext';
import orderService from '../services/orderService';
import './Checkout.css';

const Checkout = () => {
  const { cart, cartItems, clearCart } = useContext(CartContext);
  const { currentUser } = useContext(AuthContext);
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    shippingAddress: {
      fullName: currentUser ? `${currentUser.firstName} ${currentUser.lastName}` : '',
      addressLine1: '',
      addressLine2: '',
      city: '',
      state: '',
      postalCode: '',
      country: 'United States'
    },
    paymentMethod: 'credit_card',
    cardDetails: {
      cardNumber: '',
      cardName: '',
      expiryDate: '',
      cvv: ''
    }
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    
    // Handle nested objects in form data
    if (name.includes('.')) {
      const [parent, child] = name.split('.');
      setFormData(prevState => ({
        ...prevState,
        [parent]: {
          ...prevState[parent],
          [child]: value
        }
      }));
    } else {
      setFormData(prevState => ({
        ...prevState,
        [name]: value
      }));
    }
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!cartItems.length) {
      setError('Your cart is empty');
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      
      // In a real application, this would validate payment details
      // and create an order via API
      
      // Create order object
      const orderData = {
        customerId: currentUser.customerId,
        items: cartItems.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          price: item.price
        })),
        shippingAddress: formData.shippingAddress,
        paymentMethod: formData.paymentMethod,
        totalAmount: cart.totalPrice,
        status: 'pending'
      };
      
      // Create order
      const order = await orderService.createOrder(orderData);
      
      // Clear cart after successful order
      await clearCart();
      
      // Redirect to order confirmation page
      navigate(`/order-confirmation/${order.orderId}`);
      
    } catch (error) {
      console.error('Checkout error:', error);
      setError('Failed to process your order. Please try again.');
    } finally {
      setLoading(false);
    }
  };
  
  if (!cartItems.length) {
    return (
      <div className="checkout-page">
        <div className="empty-checkout">
          <h2>Your cart is empty</h2>
          <p>You need to add items to your cart before checking out.</p>
          <button 
            className="btn btn-primary"
            onClick={() => navigate('/products')}
          >
            Browse Products
          </button>
        </div>
      </div>
    );
  }
  
  return (
    <div className="checkout-page">
      <h1>Checkout</h1>
      
      {error && (
        <div className="alert alert-danger">
          {error}
        </div>
      )}
      
      <div className="checkout-container">
        <div className="checkout-form">
          <form onSubmit={handleSubmit}>
            <div className="form-section">
              <h2>Shipping Information</h2>
              
              <div className="form-group">
                <label htmlFor="fullName">Full Name</label>
                <input
                  type="text"
                  id="fullName"
                  name="shippingAddress.fullName"
                  value={formData.shippingAddress.fullName}
                  onChange={handleChange}
                  className="form-control"
                  required
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="addressLine1">Address Line 1</label>
                <input
                  type="text"
                  id="addressLine1"
                  name="shippingAddress.addressLine1"
                  value={formData.shippingAddress.addressLine1}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Street address, P.O. box, company name"
                  required
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="addressLine2">Address Line 2 (Optional)</label>
                <input
                  type="text"
                  id="addressLine2"
                  name="shippingAddress.addressLine2"
                  value={formData.shippingAddress.addressLine2}
                  onChange={handleChange}
                  className="form-control"
                  placeholder="Apartment, suite, unit, building, floor, etc."
                />
              </div>
              
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="city">City</label>
                  <input
                    type="text"
                    id="city"
                    name="shippingAddress.city"
                    value={formData.shippingAddress.city}
                    onChange={handleChange}
                    className="form-control"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="state">State</label>
                  <input
                    type="text"
                    id="state"
                    name="shippingAddress.state"
                    value={formData.shippingAddress.state}
                    onChange={handleChange}
                    className="form-control"
                    required
                  />
                </div>
              </div>
              
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="postalCode">Postal Code</label>
                  <input
                    type="text"
                    id="postalCode"
                    name="shippingAddress.postalCode"
                    value={formData.shippingAddress.postalCode}
                    onChange={handleChange}
                    className="form-control"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="country">Country</label>
                  <select
                    id="country"
                    name="shippingAddress.country"
                    value={formData.shippingAddress.country}
                    onChange={handleChange}
                    className="form-control"
                    required
                  >
                    <option value="United States">United States</option>
                    <option value="Canada">Canada</option>
                    <option value="United Kingdom">United Kingdom</option>
                    <option value="Australia">Australia</option>
                    <option value="Germany">Germany</option>
                    <option value="France">France</option>
                  </select>
                </div>
              </div>
            </div>
            
            <div className="form-section">
              <h2>Payment Method</h2>
              
              <div className="payment-methods">
                <div className="payment-method">
                  <input
                    type="radio"
                    id="credit_card"
                    name="paymentMethod"
                    value="credit_card"
                    checked={formData.paymentMethod === 'credit_card'}
                    onChange={handleChange}
                    required
                  />
                  <label htmlFor="credit_card">Credit Card</label>
                </div>
                
                <div className="payment-method">
                  <input
                    type="radio"
                    id="paypal"
                    name="paymentMethod"
                    value="paypal"
                    checked={formData.paymentMethod === 'paypal'}
                    onChange={handleChange}
                  />
                  <label htmlFor="paypal">PayPal</label>
                </div>
              </div>
              
              {formData.paymentMethod === 'credit_card' && (
                <div className="card-details">
                  <div className="form-group">
                    <label htmlFor="cardNumber">Card Number</label>
                    <input
                      type="text"
                      id="cardNumber"
                      name="cardDetails.cardNumber"
                      value={formData.cardDetails.cardNumber}
                      onChange={handleChange}
                      className="form-control"
                      placeholder="1234 5678 9012 3456"
                      required
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="cardName">Name on Card</label>
                    <input
                      type="text"
                      id="cardName"
                      name="cardDetails.cardName"
                      value={formData.cardDetails.cardName}
                      onChange={handleChange}
                      className="form-control"
                      required
                    />
                  </div>
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="expiryDate">Expiry Date</label>
                      <input
                        type="text"
                        id="expiryDate"
                        name="cardDetails.expiryDate"
                        value={formData.cardDetails.expiryDate}
                        onChange={handleChange}
                        className="form-control"
                        placeholder="MM/YY"
                        required
                      />
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="cvv">CVV</label>
                      <input
                        type="text"
                        id="cvv"
                        name="cardDetails.cvv"
                        value={formData.cardDetails.cvv}
                        onChange={handleChange}
                        className="form-control"
                        placeholder="123"
                        required
                      />
                    </div>
                  </div>
                </div>
              )}
            </div>
            
            <div className="checkout-actions">
              <button 
                type="button" 
                className="btn btn-outline"
                onClick={() => navigate('/cart')}
              >
                Back to Cart
              </button>
              
              <button 
                type="submit" 
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Processing...' : 'Place Order'}
              </button>
            </div>
          </form>
        </div>
        
        <div className="order-summary">
          <h2>Order Summary</h2>
          
          <div className="order-items">
            {cartItems.map(item => (
              <div className="order-item" key={item.productId}>
                <div className="item-info">
                  <span className="item-name">{item.name}</span>
                  <span className="item-quantity">x{item.quantity}</span>
                </div>
                <span className="item-price">${(item.price * item.quantity).toFixed(2)}</span>
              </div>
            ))}
          </div>
          
          <div className="order-totals">
            <div className="total-row">
              <span>Subtotal</span>
              <span>${cart.totalPrice.toFixed(2)}</span>
            </div>
            <div className="total-row">
              <span>Shipping</span>
              <span>Free</span>
            </div>
            <div className="total-row">
              <span>Tax</span>
              <span>${(cart.totalPrice * 0.1).toFixed(2)}</span>
            </div>
            <div className="total-row grand-total">
              <span>Total</span>
              <span>${(cart.totalPrice * 1.1).toFixed(2)}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;






























































































































































