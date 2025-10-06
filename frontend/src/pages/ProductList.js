













































































































import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import ProductCard from '../components/ProductCard';
import productService from '../services/productService';
import categoryService from '../services/categoryService';
import './ProductList.css';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchParams, setSearchParams] = useSearchParams();
  
  // Filter states
  const [selectedCategory, setSelectedCategory] = useState(searchParams.get('category') || '');
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [sortOrder, setSortOrder] = useState('asc');
  
  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [productsPerPage] = useState(8);
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch all products
        let productsData;
        if (selectedCategory) {
          productsData = await productService.getProductsByCategory(selectedCategory);
        } else {
          productsData = await productService.getAllProducts();
        }
        
        setProducts(productsData);
        
        // Fetch all categories
        const categoriesData = await categoryService.getAllCategories();
        setCategories(categoriesData);
        
        setLoading(false);
      } catch (error) {
        console.error('Error fetching products:', error);
        setError('Failed to load products. Please try again later.');
        setLoading(false);
      }
    };

    fetchData();
  }, [selectedCategory]);
  
  // Update URL when category changes
  useEffect(() => {
    if (selectedCategory) {
      setSearchParams({ category: selectedCategory });
    } else {
      setSearchParams({});
    }
  }, [selectedCategory, setSearchParams]);
  
  // Filter and sort products
  const filteredProducts = products
    .filter(product => 
      product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (product.description && product.description.toLowerCase().includes(searchTerm.toLowerCase()))
    )
    .sort((a, b) => {
      if (sortBy === 'price') {
        return sortOrder === 'asc' ? a.price - b.price : b.price - a.price;
      } else {
        return sortOrder === 'asc' 
          ? a.name.localeCompare(b.name)
          : b.name.localeCompare(a.name);
      }
    });
  
  // Pagination logic
  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = filteredProducts.slice(indexOfFirstProduct, indexOfLastProduct);
  const totalPages = Math.ceil(filteredProducts.length / productsPerPage);
  
  // Change page
  const paginate = (pageNumber) => setCurrentPage(pageNumber);
  
  // Handle category filter change
  const handleCategoryChange = (e) => {
    setSelectedCategory(e.target.value);
    setCurrentPage(1);
  };
  
  // Handle search input change
  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
    setCurrentPage(1);
  };
  
  // Handle sort change
  const handleSortChange = (e) => {
    const value = e.target.value;
    if (value === 'price-asc') {
      setSortBy('price');
      setSortOrder('asc');
    } else if (value === 'price-desc') {
      setSortBy('price');
      setSortOrder('desc');
    } else if (value === 'name-asc') {
      setSortBy('name');
      setSortOrder('asc');
    } else if (value === 'name-desc') {
      setSortBy('name');
      setSortOrder('desc');
    }
    setCurrentPage(1);
  };
  
  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading products...</p>
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
    <div className="product-list-page">
      <h1>Products</h1>
      
      {/* Filters */}
      <div className="filters">
        <div className="filter-group">
          <label htmlFor="category-filter">Category:</label>
          <select 
            id="category-filter" 
            value={selectedCategory} 
            onChange={handleCategoryChange}
            className="form-control"
          >
            <option value="">All Categories</option>
            {categories.map(category => (
              <option key={category.categoryId} value={category.categoryId}>
                {category.name}
              </option>
            ))}
          </select>
        </div>
        
        <div className="filter-group">
          <label htmlFor="search-filter">Search:</label>
          <input 
            type="text" 
            id="search-filter" 
            value={searchTerm} 
            onChange={handleSearchChange}
            placeholder="Search products..."
            className="form-control"
          />
        </div>
        
        <div className="filter-group">
          <label htmlFor="sort-filter">Sort By:</label>
          <select 
            id="sort-filter" 
            value={`${sortBy}-${sortOrder}`} 
            onChange={handleSortChange}
            className="form-control"
          >
            <option value="name-asc">Name (A-Z)</option>
            <option value="name-desc">Name (Z-A)</option>
            <option value="price-asc">Price (Low to High)</option>
            <option value="price-desc">Price (High to Low)</option>
          </select>
        </div>
      </div>
      
      {/* Product Grid */}
      {currentProducts.length > 0 ? (
        <div className="products-grid">
          {currentProducts.map(product => (
            <div className="product-item" key={product.productId}>
              <ProductCard product={product} />
            </div>
          ))}
        </div>
      ) : (
        <div className="no-products">
          <p>No products found matching your criteria.</p>
          <button 
            className="btn btn-primary"
            onClick={() => {
              setSelectedCategory('');
              setSearchTerm('');
              setSortBy('name');
              setSortOrder('asc');
            }}
          >
            Clear Filters
          </button>
        </div>
      )}
      
      {/* Pagination */}
      {filteredProducts.length > productsPerPage && (
        <div className="pagination">
          <button 
            onClick={() => paginate(currentPage - 1)}
            disabled={currentPage === 1}
            className="pagination-btn"
          >
            &laquo; Previous
          </button>
          
          <div className="page-numbers">
            {Array.from({ length: totalPages }, (_, i) => i + 1).map(number => (
              <button
                key={number}
                onClick={() => paginate(number)}
                className={`pagination-btn ${currentPage === number ? 'active' : ''}`}
              >
                {number}
              </button>
            ))}
          </div>
          
          <button 
            onClick={() => paginate(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="pagination-btn"
          >
            Next &raquo;
          </button>
        </div>
      )}
    </div>
  );
};

export default ProductList;













































































































