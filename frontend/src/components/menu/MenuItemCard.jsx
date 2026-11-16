import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { formatPrice, getMenuItemImage } from '../../utils/constants';
import { Tag, Plus, Minus } from 'lucide-react';

/**
 * Zomato/Swiggy styled Food Item Card
 * Fully integrated with CartContext in Phase 4:
 * - Shows "ADD" button when not in cart
 * - Shows interactive "[-] qty [+]" controls when already in cart
 * - Handles single-restaurant conflict checking
 */
const MenuItemCard = ({ item, isRestaurantOpen = true }) => {
  const { id, name, description, price, category, imageUrl, available } = item;
  const { addToCart, updateQuantity, getCartItem, getItemQuantity, loading } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const displayImage = getMenuItemImage(imageUrl, category, id, name);
  const isAvailable = available && isRestaurantOpen;

  const currentQty = getItemQuantity(id);
  const cartItem = getCartItem(id);

  const handleAdd = async () => {
    if (!isAuthenticated) {
      // Redirect to login and preserve page location
      navigate('/login', { state: { from: location } });
      return;
    }
    await addToCart(item, 1);
  };

  const handleIncrement = () => {
    if (cartItem) {
      updateQuantity(cartItem.cartItemId, currentQty + 1);
    }
  };

  const handleDecrement = () => {
    if (cartItem) {
      updateQuantity(cartItem.cartItemId, currentQty - 1);
    }
  };

  return (
    <div className="bg-white rounded-2xl p-4 sm:p-5 border border-gray-200/80 shadow-sm hover:shadow-md transition-all duration-200 flex flex-col-reverse sm:flex-row justify-between gap-4 items-start sm:items-center">
      
      {/* Details Column */}
      <div className="flex-1 space-y-1.5">
        
        {/* Category Pill & Availability */}
        <div className="flex items-center space-x-2">
          {category && (
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-orange-50 text-brand-700">
              <Tag className="w-3 h-3 mr-1" />
              {category}
            </span>
          )}
          {!isAvailable && (
            <span className="px-2 py-0.5 rounded-full text-xs font-semibold bg-gray-100 text-gray-500">
              {!isRestaurantOpen ? 'Restaurant Closed' : 'Sold Out'}
            </span>
          )}
        </div>

        {/* Dish Name */}
        <h4 className="text-base sm:text-lg font-bold text-gray-900 tracking-tight">
          {name}
        </h4>

        {/* Price */}
        <p className="text-base font-extrabold text-gray-900">
          {formatPrice(price)}
        </p>

        {/* Description */}
        <p className="text-xs sm:text-sm text-gray-500 line-clamp-2 leading-relaxed">
          {description || 'Freshly prepared with authentic ingredients and mouth-watering spices.'}
        </p>
      </div>

      {/* Image & Action Column */}
      <div className="relative w-full sm:w-32 h-40 sm:h-32 flex-shrink-0 rounded-2xl overflow-hidden bg-gray-100 shadow-inner group">
        <img
          src={displayImage}
          alt={name}
          className={`w-full h-full object-cover transition-transform duration-300 group-hover:scale-105 ${
            !isAvailable ? 'grayscale opacity-60' : ''
          }`}
          loading="lazy"
        />

        {/* Action Controls on bottom of image */}
        <div className="absolute bottom-2 left-1/2 transform -translate-x-1/2">
          {!isAvailable ? (
            <span className="px-3 py-1 rounded-full text-xs font-bold bg-gray-200/90 text-gray-400 border border-gray-300 cursor-not-allowed uppercase tracking-wider">
              CLOSED
            </span>
          ) : currentQty > 0 ? (
            /* Quantity Modifier Pill */
            <div className="flex items-center bg-white rounded-full shadow-lg border border-brand-200 overflow-hidden">
              <button
                type="button"
                onClick={handleDecrement}
                disabled={loading}
                className="px-2 py-1 text-brand-600 hover:bg-orange-50 font-bold transition disabled:opacity-40"
                title="Decrease quantity"
              >
                <Minus className="w-3 h-3" />
              </button>
              <span className="px-2 text-xs font-extrabold text-gray-900 min-w-[1.25rem] text-center">
                {currentQty}
              </span>
              <button
                type="button"
                onClick={handleIncrement}
                disabled={loading}
                className="px-2 py-1 text-brand-600 hover:bg-orange-50 font-bold transition disabled:opacity-40"
                title="Increase quantity"
              >
                <Plus className="w-3 h-3" />
              </button>
            </div>
          ) : (
            /* Initial Add to Cart Button */
            <button
              type="button"
              onClick={handleAdd}
              disabled={loading}
              className="px-4 py-1.5 rounded-full text-xs font-bold bg-white hover:bg-brand-50 text-brand-600 border border-brand-200 shadow-md transition-all uppercase tracking-wider transform hover:scale-105 active:scale-95"
            >
              ADD +
            </button>
          )}
        </div>
      </div>

    </div>
  );
};

export default MenuItemCard;
