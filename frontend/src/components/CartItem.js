
















































































import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { CartContext } from '../context/CartContext';
import './CartItem.css';

const CartItem = ({ item }) => {
  const { updateCartItem, removeFromCart } = useContext(CartContext);
  
  const handleQuantityChange = (e) => {
    const newQuantity = parseInt(e.target.value);
    if (newQuantity > 0) {
      updateCartItem(item.productId, newQuantity);
    }
  };
  
  const handleRemove = () => {
    removeFromCart(item.productId);
  };
  
  const incrementQuantity = () => {
    updateCartItem(item.productId, item.quantity + 1);
  };
  
  const decrementQuantity = () => {
    if (item.quantity > 1) {
      updateCartItem(item.productId, item.quantity - 1);
    }
  };
  
  return (
    <div className="cart-item">
      <div className="cart-item-image">
        {item.imageUrl ? (
          <img src={item.imageUrl} alt={item.name} />
        ) : (
          <div className="placeholder-image">No Image</div>
        )}
      </div>
      
      <div className="cart-item-details">
        <Link to={`/products/${item.productId}`} className="cart-item-name">
          {item.name}
        </Link>
        <p className="cart-item-price">${item.price.toFixed(2)}</p>
      </div>
      
      <div className="cart-item-quantity">
        <div className="quantity-control">
          <button 
            className="quantity-btn"
            onClick={decrementQuantity}
            disabled={item.quantity <= 1}
          >
            -
          </button>
          <input
            type="number"
            min="1"
            value={item.quantity}
            onChange={handleQuantityChange}
            className="quantity-input"
          />
          <button 
            className="quantity-btn"
            onClick={incrementQuantity}
          >
            +
          </button>
        </div>
      </div>
      
      <div className="cart-item-subtotal">
        <p>${(item.price * item.quantity).toFixed(2)}</p>
      </div>
      
      <div className="cart-item-actions">
        <button 
          className="btn btn-danger btn-sm remove-btn"
          onClick={handleRemove}
        >
          <i className="fas fa-trash"></i>
        </button>
      </div>
    </div>
  );
};

export default CartItem;
















































































