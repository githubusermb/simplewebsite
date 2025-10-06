
















































































































































































import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import orderService from '../services/orderService';
import './OrderConfirmation.css';

const OrderConfirmation = () => {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    const fetchOrder = async () => {
      try {
        setLoading(true);
        const orderData = await orderService.getOrder(orderId);
        setOrder(orderData);
        setLoading(false);
      } catch (error) {
        console.error(`Error fetching order ${orderId}:`, error);
        setError('Failed to load order details. Please try again later.');
        setLoading(false);
      }
    };

    fetchOrder();
  }, [orderId]);
  
  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading order details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <Link to="/order-history" className="btn btn-primary">
          View Order History
        </Link>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="error-container">
        <p className="error-message">Order not found.</p>
        <Link to="/order-history" className="btn btn-primary">
          View Order History
        </Link>
      </div>
    );
  }

  // Format date
  const orderDate = new Date(order.createdAt).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
  
  // Calculate estimated delivery date (7 days from order date)
  const deliveryDate = new Date(order.createdAt);
  deliveryDate.setDate(deliveryDate.getDate() + 7);
  const formattedDeliveryDate = deliveryDate.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });

  return (
    <div className="order-confirmation-page">
      <div className="confirmation-header">
        <div className="confirmation-icon">
          <i className="fas fa-check-circle"></i>
        </div>
        <h1>Order Confirmed!</h1>
        <p>Thank you for your purchase. Your order has been received.</p>
      </div>
      
      <div className="order-details">
        <div className="order-info">
          <div className="info-item">
            <span className="info-label">Order Number:</span>
            <span className="info-value">{order.orderId}</span>
          </div>
          <div className="info-item">
            <span className="info-label">Order Date:</span>
            <span className="info-value">{orderDate}</span>
          </div>
          <div className="info-item">
            <span className="info-label">Payment Method:</span>
            <span className="info-value">{order.paymentMethod}</span>
          </div>
          <div className="info-item">
            <span className="info-label">Order Status:</span>
            <span className={`info-value status-${order.status}`}>{order.status}</span>
          </div>
          <div className="info-item">
            <span className="info-label">Estimated Delivery:</span>
            <span className="info-value">{formattedDeliveryDate}</span>
          </div>
        </div>
        
        <div className="shipping-address">
          <h3>Shipping Address</h3>
          <p>{order.shippingAddress.fullName}</p>
          <p>{order.shippingAddress.addressLine1}</p>
          {order.shippingAddress.addressLine2 && <p>{order.shippingAddress.addressLine2}</p>}
          <p>
            {order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.postalCode}
          </p>
          <p>{order.shippingAddress.country}</p>
        </div>
      </div>
      
      <div className="order-items-container">
        <h3>Order Items</h3>
        
        <div className="order-items">
          {order.items.map(item => (
            <div className="order-item" key={item.productId}>
              <div className="item-info">
                <span className="item-name">{item.name}</span>
                <span className="item-quantity">Quantity: {item.quantity}</span>
              </div>
              <span className="item-price">${(item.price * item.quantity).toFixed(2)}</span>
            </div>
          ))}
        </div>
        
        <div className="order-summary">
          <div className="summary-row">
            <span>Subtotal</span>
            <span>${order.totalAmount.toFixed(2)}</span>
          </div>
          <div className="summary-row">
            <span>Shipping</span>
            <span>Free</span>
          </div>
          <div className="summary-row">
            <span>Tax</span>
            <span>${(order.totalAmount * 0.1).toFixed(2)}</span>
          </div>
          <div className="summary-row total">
            <span>Total</span>
            <span>${(order.totalAmount * 1.1).toFixed(2)}</span>
          </div>
        </div>
      </div>
      
      <div className="confirmation-actions">
        <Link to="/products" className="btn btn-outline">
          Continue Shopping
        </Link>
        <Link to="/order-history" className="btn btn-primary">
          View Order History
        </Link>
      </div>
    </div>
  );
};

export default OrderConfirmation;
















































































































































































