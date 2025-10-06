




































import React from 'react';
import { Link } from 'react-router-dom';
import './Footer.css';

const Footer = () => {
  const currentYear = new Date().getFullYear();
  
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-content">
          <div className="footer-section about">
            <h3>ShopCart</h3>
            <p>Your one-stop shop for all your shopping needs. We provide quality products at affordable prices.</p>
            <div className="contact">
              <span><i className="fas fa-phone"></i> &nbsp; 123-456-7890</span>
              <span><i className="fas fa-envelope"></i> &nbsp; info@shopcart.com</span>
            </div>
            <div className="socials">
              <a href="#"><i className="fab fa-facebook"></i></a>
              <a href="#"><i className="fab fa-twitter"></i></a>
              <a href="#"><i className="fab fa-instagram"></i></a>
              <a href="#"><i className="fab fa-linkedin"></i></a>
            </div>
          </div>
          
          <div className="footer-section links">
            <h3>Quick Links</h3>
            <ul>
              <li><Link to="/">Home</Link></li>
              <li><Link to="/products">Products</Link></li>
              <li><Link to="/cart">Cart</Link></li>
              <li><Link to="/login">Login</Link></li>
              <li><Link to="/register">Register</Link></li>
            </ul>
          </div>
          
          <div className="footer-section contact-form">
            <h3>Contact Us</h3>
            <form>
              <input type="email" name="email" className="contact-input" placeholder="Your email address..." />
              <textarea name="message" className="contact-input" placeholder="Your message..."></textarea>
              <button type="submit" className="btn btn-primary">Send</button>
            </form>
          </div>
        </div>
        
        <div className="footer-bottom">
          <p>&copy; {currentYear} ShopCart. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;




































