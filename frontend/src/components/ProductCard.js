




















































import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { CartContext } from '../context/CartContext';
import './ProductCard.css';

const ProductCard = ({ product }) => {
  const { addToCart } = useContext(CartContext);
  
  const handleAddToCart = (e) => {
    e.preventDefault();
    addToCart(product, 1);
  };
  
  return (
    <div className="product-card">
      <Link to={`/products/${product.productId}`} className="product-link">
        <div className="product-image">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <div className="placeholder-image">No Image</div>
          )}
        </div>
        
        <div className="product-info">
          <h3 className="product-name">{product.name}</h3>
          <p className="product-price">${product.price.toFixed(2)}</p>
          
          {product.inventory > 0 ? (
            <span className="product-status in-stock">In Stock</span>
          ) : (
            <span className="product-status out-of-stock">Out of Stock</span>
          )}
        </div>
      </Link>
      
      <div className="product-actions">
        <button 
          className="btn btn-primary add-to-cart"
          onClick={handleAddToCart}
          disabled={product.inventory <= 0}
        >
          Add to Cart
        </button>
      </div>
    </div>
  );
};

export default ProductCard;




















































