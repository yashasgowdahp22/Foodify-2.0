import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { 
  UtensilsCrossed, 
  Search, 
  ShoppingBag, 
  User, 
  LogOut, 
  Menu, 
  X, 
  Clock
} from 'lucide-react';

/**
 * Modern Swiggy/Zomato inspired Navbar
 * - Sticky navigation at top
 * - Shows brand logo, search link, cart pill, and user profile/logout
 * - Fully responsive with mobile slide-down menu
 */
const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [userDropdownOpen, setUserDropdownOpen] = useState(false);

  // Dynamic live cart item count from CartContext
  const cartItemCount = cart?.itemCount || 0;

  const handleLogout = async () => {
    await logout();
    setUserDropdownOpen(false);
    setMobileMenuOpen(false);
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-gray-100 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          
          {/* Logo & Brand Name */}
          <Link to="/" className="flex items-center space-x-2 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-brand-600 to-amber-500 flex items-center justify-center text-white shadow-md shadow-brand-500/20 group-hover:scale-105 transition-transform duration-200">
              <UtensilsCrossed className="w-5 h-5" />
            </div>
            <div className="flex flex-col">
              <span className="text-2xl font-bold tracking-tight text-gray-900 group-hover:text-brand-600 transition-colors">
                Foodify<span className="text-brand-500 font-extrabold text-sm ml-0.5">2.0</span>
              </span>
            </div>
          </Link>

          {/* Desktop Navigation Links */}
          <nav className="hidden md:flex items-center space-x-6">
            <Link 
              to="/search" 
              className="flex items-center space-x-1.5 text-sm font-medium text-gray-700 hover:text-brand-600 transition-colors px-3 py-2 rounded-lg hover:bg-orange-50/50"
            >
              <Search className="w-4 h-4 text-gray-500" />
              <span>Search Dishes & Restaurants</span>
            </Link>

            {/* Cart Link */}
            <Link
              to="/cart"
              className="relative flex items-center space-x-2 text-sm font-medium px-4 py-2 rounded-full bg-brand-50 text-brand-700 hover:bg-brand-100 transition-colors"
            >
              <ShoppingBag className="w-4 h-4 text-brand-600" />
              <span>Cart</span>
              {cartItemCount > 0 && (
                <span className="ml-1 px-2 py-0.5 text-xs font-bold rounded-full bg-brand-600 text-white">
                  {cartItemCount}
                </span>
              )}
            </Link>

            {/* My Orders Link (Customer) */}
            {isAuthenticated && (
              <Link
                to="/orders"
                className="flex items-center space-x-1.5 text-sm font-medium text-gray-700 hover:text-brand-600 transition-colors px-3 py-2 rounded-lg hover:bg-orange-50/50"
              >
                <Clock className="w-4 h-4 text-gray-500" />
                <span>My Orders</span>
              </Link>
            )}

            {/* Authenticated User Menu or Guest Buttons */}
            {isAuthenticated ? (
              <div className="relative">
                <button
                  onClick={() => setUserDropdownOpen(!userDropdownOpen)}
                  className="flex items-center space-x-2 text-sm font-medium text-gray-700 hover:text-brand-600 py-1.5 px-3 rounded-lg hover:bg-gray-100 transition"
                >
                  <div className="w-8 h-8 rounded-full bg-brand-100 text-brand-700 flex items-center justify-center font-semibold text-sm">
                    {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
                  </div>
                  <span className="max-w-[120px] truncate">{user?.name}</span>
                </button>

                {/* Dropdown Menu */}
                {userDropdownOpen && (
                  <div 
                    className="absolute right-0 mt-2 w-52 bg-white rounded-xl shadow-lg border border-gray-100 py-1.5 z-50 animate-in fade-in slide-in-from-top-2 duration-150"
                    onMouseLeave={() => setUserDropdownOpen(false)}
                  >
                    <div className="px-4 py-2 border-b border-gray-100">
                      <p className="text-xs text-gray-500">Signed in as</p>
                      <p className="text-sm font-semibold text-gray-900 truncate">{user?.email}</p>
                    </div>

                    <Link
                      to="/profile"
                      onClick={() => setUserDropdownOpen(false)}
                      className="flex items-center space-x-2 px-4 py-2 text-sm text-gray-700 hover:bg-orange-50 hover:text-brand-600 transition-colors"
                    >
                      <User className="w-4 h-4" />
                      <span>My Profile</span>
                    </Link>

                    <Link
                      to="/orders"
                      onClick={() => setUserDropdownOpen(false)}
                      className="flex items-center space-x-2 px-4 py-2 text-sm text-gray-700 hover:bg-orange-50 hover:text-brand-600 transition-colors"
                    >
                      <Clock className="w-4 h-4" />
                      <span>Orders & Receipts</span>
                    </Link>

                    <div className="border-t border-gray-100 my-1"></div>

                    <button
                      onClick={handleLogout}
                      className="w-full flex items-center space-x-2 px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors text-left"
                    >
                      <LogOut className="w-4 h-4" />
                      <span>Sign Out</span>
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex items-center space-x-3">
                <Link
                  to="/login"
                  className="text-sm font-semibold text-gray-700 hover:text-brand-600 px-3 py-2 rounded-lg hover:bg-gray-50 transition"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="text-sm font-semibold text-white bg-brand-500 hover:bg-brand-600 px-4 py-2 rounded-full shadow-sm shadow-brand-500/30 transition transform hover:-translate-y-0.5 active:translate-y-0"
                >
                  Sign Up
                </Link>
              </div>
            )}
          </nav>

          {/* Mobile Hamburger Button */}
          <div className="flex md:hidden items-center space-x-2">
            <Link
              to="/cart"
              className="relative p-2 text-gray-600 hover:text-brand-600 rounded-lg"
            >
              <ShoppingBag className="w-5 h-5" />
              {cartItemCount > 0 && (
                <span className="absolute top-1 right-1 w-4 h-4 rounded-full bg-brand-500 text-white text-[10px] font-bold flex items-center justify-center">
                  {cartItemCount}
                </span>
              )}
            </Link>
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 text-gray-600 hover:text-gray-900 rounded-lg hover:bg-gray-100 transition"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>

        </div>
      </div>

      {/* Mobile Slide-down Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-gray-100 bg-white px-4 pt-3 pb-5 space-y-2">
          <Link
            to="/search"
            onClick={() => setMobileMenuOpen(false)}
            className="flex items-center space-x-3 px-3 py-2.5 rounded-lg text-base font-medium text-gray-700 hover:bg-orange-50 hover:text-brand-600"
          >
            <Search className="w-5 h-5 text-gray-500" />
            <span>Search Dishes &amp; Restaurants</span>
          </Link>

          <Link
            to="/cart"
            onClick={() => setMobileMenuOpen(false)}
            className="flex items-center justify-between px-3 py-2.5 rounded-lg text-base font-medium text-gray-700 hover:bg-orange-50 hover:text-brand-600"
          >
            <div className="flex items-center space-x-3">
              <ShoppingBag className="w-5 h-5 text-gray-500" />
              <span>My Cart</span>
            </div>
            {cartItemCount > 0 && (
              <span className="px-2 py-0.5 text-xs font-bold rounded-full bg-brand-600 text-white">
                {cartItemCount}
              </span>
            )}
          </Link>

          {isAuthenticated ? (
            <>
              <div className="border-t border-gray-100 pt-2 mt-2">
                <p className="px-3 text-xs font-semibold text-gray-400 uppercase tracking-wider">Account</p>
                <p className="px-3 py-1 text-sm font-medium text-gray-800">{user?.name} ({user?.email})</p>
              </div>

              <Link
                to="/profile"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center space-x-3 px-3 py-2 rounded-lg text-base font-medium text-gray-700 hover:bg-orange-50 hover:text-brand-600"
              >
                <User className="w-5 h-5" />
                <span>My Profile</span>
              </Link>

              <Link
                to="/orders"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center space-x-3 px-3 py-2 rounded-lg text-base font-medium text-gray-700 hover:bg-orange-50 hover:text-brand-600"
              >
                <Clock className="w-5 h-5" />
                <span>My Orders</span>
              </Link>

              <button
                onClick={handleLogout}
                className="w-full flex items-center space-x-3 px-3 py-2 rounded-lg text-base font-medium text-red-600 hover:bg-red-50 text-left"
              >
                <LogOut className="w-5 h-5" />
                <span>Sign Out</span>
              </button>
            </>
          ) : (
            <div className="pt-2 border-t border-gray-100 flex flex-col space-y-2">
              <Link
                to="/login"
                onClick={() => setMobileMenuOpen(false)}
                className="w-full text-center py-2.5 rounded-lg font-semibold text-gray-700 hover:bg-gray-100"
              >
                Sign In
              </Link>
              <Link
                to="/register"
                onClick={() => setMobileMenuOpen(false)}
                className="w-full text-center py-2.5 rounded-full font-semibold text-white bg-brand-500 hover:bg-brand-600 shadow-md shadow-brand-500/20"
              >
                Create Account
              </Link>
            </div>
          )}
        </div>
      )}
    </header>
  );
};

export default Navbar;

