import React from 'react';
import { UtensilsCrossed, Heart } from 'lucide-react';
import { Link } from 'react-router-dom';

/**
 * Modern Clean Footer
 */
const Footer = () => {
  return (
    <footer className="bg-gray-900 text-gray-400 mt-auto border-t border-gray-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          
          {/* Brand Info */}
          <div className="md:col-span-2 space-y-4">
            <div className="flex items-center space-x-2">
              <div className="w-8 h-8 rounded-lg bg-brand-500 flex items-center justify-center text-white">
                <UtensilsCrossed className="w-4 h-4" />
              </div>
              <span className="text-xl font-bold text-white tracking-tight">
                Foodify<span className="text-brand-500 font-extrabold text-xs ml-0.5">2.0</span>
              </span>
            </div>
            <p className="text-sm text-gray-400 max-w-sm">
              Discover and order the best food from top-rated restaurants near you. Fast delivery, simulated payments, and real-time order tracking.
            </p>
            <div className="text-xs text-gray-500">
              Built with <span className="text-emerald-400 font-medium">Spring Boot 3</span> &amp; <span className="text-cyan-400 font-medium">React + Tailwind</span>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Quick Links</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <Link to="/" className="hover:text-brand-400 transition-colors">Restaurants</Link>
              </li>
              <li>
                <Link to="/search" className="hover:text-brand-400 transition-colors">Search Dishes</Link>
              </li>
              <li>
                <Link to="/cart" className="hover:text-brand-400 transition-colors">My Cart</Link>
              </li>
            </ul>
          </div>

          {/* Customer Support & Security */}
          <div>
            <h4 className="text-sm font-semibold text-white uppercase tracking-wider mb-4">Customer Account</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <Link to="/profile" className="hover:text-brand-400 transition-colors">My Profile</Link>
              </li>
              <li>
                <Link to="/orders" className="hover:text-brand-400 transition-colors">Order History</Link>
              </li>
              <li>
                <Link to="/login" className="hover:text-brand-400 transition-colors">Sign In / Sign Up</Link>
              </li>
            </ul>
          </div>

        </div>

        {/* Bottom Bar */}
        <div className="border-t border-gray-800 pt-6 flex flex-col sm:flex-row items-center justify-between text-xs text-gray-500">
          <p>© {new Date().getFullYear()} Foodify 2.0. All rights reserved.</p>
          <p className="mt-2 sm:mt-0 flex items-center">
            Crafted with <Heart className="w-3.5 h-3.5 text-red-500 mx-1 fill-current" /> for food lovers.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;

