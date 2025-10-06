




























import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { CartContext } from '../context/CartContext';
import './Header.css';

const Header = () => {
  const { currentUser, logout } = useContext(AuthContext);
  const { cart } = useContext(CartContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <div className="logo">
            <Link to="/">
              <h1>ShopCart</h1>
            </Link>
          </div>
          
          <nav className="nav-menu">
            <ul>
              <li>
                <Link to="/">Home</Link>
              </li>
              <li>
                <Link to="/products">Products</Link>
              </li>
            </ul>
          </nav>
          
          <div className="header-actions">
            <div className="cart-icon">
              <Link to="/cart">
                <i className="fas fa-shopping-cart"></i>
                {cart && cart.totalItems > 0 && (
                  <span className="cart-badge">{cart.totalItems}</span>
                )}
              </Link>
            </div>
            
            {currentUser ? (
              <div className="user-menu">
                <span className="user-name">Hello, {currentUser.firstName}</span>
                <div className="dropdown-menu">
                  <Link to="/profile">My Profile</Link>
                  <Link to="/order-history">My Orders</Link>
                  <button onClick={handleLogout}>Logout</button>
                </div>
              </div>
            ) : (
              <div className="auth-buttons">
                <Link to="/login" className="btn btn-outline">Login</Link>
                <Link to="/register" className="btn btn-primary">Register</Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;




























