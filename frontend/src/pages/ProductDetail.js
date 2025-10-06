
























































































































import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CartContext } from '../context/CartContext';
import productService from '../services/productService';
import './ProductDetail.css';

const ProductDetail = () => {
  const { productId } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useContext(CartContext);
  
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [addedToCart, setAddedToCart] = useState(false);
  
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const productData = await productService.getProduct(productId);
        setProduct(productData);
        setLoading(false);
      } catch (error) {
        console.error(`Error fetching product ${productId}:`, error);
        setError('Failed to load product details. Please try again later.');
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);
  
  const handleQuantityChange = (e) => {
    const value = parseInt(e.target.value);
    if (value > 0) {
      setQuantity(value);
    }
  };
  
  const incrementQuantity = () => {
    setQuantity(prevQuantity => prevQuantity + 1);
  };
  
  const decrementQuantity = () => {
    if (quantity > 1) {
      setQuantity(prevQuantity => prevQuantity - 1);
    }
  };
  
  const handleAddToCart = () => {
    addToCart(product, quantity);
    setAddedToCart(true);
    
    // Reset the "Added to cart" message after 3 seconds
    setTimeout(() => {
      setAddedToCart(false);
    }, 3000);
  };
  
  const handleGoBack = () => {
    navigate(-1);
  };
  
  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading product details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <button 
          className="btn btn-primary"
          onClick={() => window.location.reload()}
        >
          Retry
        </button>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="error-container">
        <p className="error-message">Product not found.</p>
        <button 
          className="btn btn-primary"
          onClick={handleGoBack}
        >
          Go Back
        </button>
      </div>
    );
  }

  return (
    <div className="product-detail-page">
      <button 
        className="btn btn-outline back-button"
        onClick={handleGoBack}
      >
        &larr; Back
      </button>
      
      <div className="product-detail">
        <div className="product-image">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <div className="placeholder-image">No Image Available</div>
          )}
        </div>
        
        <div className="product-info">
          <h1 className="product-name">{product.name}</h1>
          
          <div className="product-price">
            <span className="price">${product.price.toFixed(2)}</span>
            {product.inventory > 0 ? (
              <span className="stock in-stock">In Stock</span>
            ) : (
              <span className="stock out-of-stock">Out of Stock</span>
            )}
          </div>
          
          <div className="product-description">
            <h3>Description</h3>
            <p>{product.description || 'No description available.'}</p>
          </div>
          
          {product.inventory > 0 && (
            <div className="product-actions">
              <div className="quantity-control">
                <label htmlFor="quantity">Quantity:</label>
                <div className="quantity-input-group">
                  <button 
                    className="quantity-btn"
                    onClick={decrementQuantity}
                    disabled={quantity <= 1}
                  >
                    -
                  </button>
                  <input
                    type="number"
                    id="quantity"
                    min="1"
                    value={quantity}
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
              
              <button 
                className="btn btn-primary add-to-cart-btn"
                onClick={handleAddToCart}
              >
                Add to Cart
              </button>
              
              {addedToCart && (
                <div className="added-to-cart">
                  <p>Added to cart! <a href="/cart">View Cart</a></p>
                </div>
              )}
            </div>
          )}
          
          {product.inventory <= 0 && (
            <div className="out-of-stock-message">
              <p>This product is currently out of stock.</p>
            </div>
          )}
          
          {product.specifications && (
            <div className="product-specifications">
              <h3>Specifications</h3>
              <ul>
                {Object.entries(product.specifications).map(([key, value]) => (
                  <li key={key}>
                    <strong>{key}:</strong> {value}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
























































































































