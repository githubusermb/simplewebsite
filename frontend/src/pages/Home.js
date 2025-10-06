






























































































import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import ProductCard from '../components/ProductCard';
import productService from '../services/productService';
import categoryService from '../services/categoryService';
import './Home.css';

const Home = () => {
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // In a real application, we would fetch featured products
        // For now, we'll just get all products and take the first few
        const productsResponse = await productService.getAllProducts();
        setFeaturedProducts(productsResponse.slice(0, 4));
        
        const categoriesResponse = await categoryService.getAllCategories();
        setCategories(categoriesResponse);
        
        setLoading(false);
      } catch (error) {
        console.error('Error fetching data for homepage:', error);
        setError('Failed to load homepage data. Please try again later.');
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading...</p>
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

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <h1>Welcome to ShopCart</h1>
          <p>Your one-stop shop for all your shopping needs</p>
          <Link to="/products" className="btn btn-primary btn-lg">
            Shop Now
          </Link>
        </div>
      </section>

      {/* Featured Products Section */}
      <section className="featured-products">
        <div className="section-header">
          <h2>Featured Products</h2>
          <Link to="/products" className="view-all">
            View All
          </Link>
        </div>
        
        {featuredProducts.length > 0 ? (
          <div className="products-grid">
            {featuredProducts.map(product => (
              <div className="product-item" key={product.productId}>
                <ProductCard product={product} />
              </div>
            ))}
          </div>
        ) : (
          <p className="no-products">No featured products available.</p>
        )}
      </section>

      {/* Categories Section */}
      <section className="categories-section">
        <div className="section-header">
          <h2>Shop by Category</h2>
        </div>
        
        {categories.length > 0 ? (
          <div className="categories-grid">
            {categories.map(category => (
              <Link 
                to={`/products?category=${category.categoryId}`} 
                className="category-card"
                key={category.categoryId}
              >
                <div className="category-content">
                  <h3>{category.name}</h3>
                  <p>{category.description}</p>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <p className="no-categories">No categories available.</p>
        )}
      </section>

      {/* Promotional Banner */}
      <section className="promo-banner">
        <div className="promo-content">
          <h2>Special Offer</h2>
          <p>Get 20% off on your first order!</p>
          <Link to="/products" className="btn btn-light">
            Shop Now
          </Link>
        </div>
      </section>
    </div>
  );
};

export default Home;






























































































