import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import './index.css';

/**
 * Frontend Application Root Entry Point
 * 
 * Hierarchy:
 * 1. BrowserRouter: Enables client-side URL routing without full page reloads.
 * 2. AuthProvider: Provides global JWT session & user state across the entire app.
 * 3. CartProvider: Provides live Cart state & operations across the entire app.
 * 4. App: Renders Navbar, current Page, and Footer.
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <App />
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);

