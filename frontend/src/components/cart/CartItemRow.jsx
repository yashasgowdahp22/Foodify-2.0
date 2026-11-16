import React from 'react';
import { useCart } from '../../context/CartContext';
import { formatPrice, getMenuItemImage } from '../../utils/constants';
import { Plus, Minus, Trash2 } from 'lucide-react';

/**
 * Cart Item Row Component
 * Displays dish details, quantity modifier controls, item subtotal, and delete button.
 */
const CartItemRow = ({ item }) => {
  const { updateQuantity, removeCartItem, loading } = useCart();
  const {
    cartItemId,
    menuItemId,
    menuItemName,
    price,
    quantity,
    subTotal,
    category,
    imageUrl,
  } = item;

  const displayImage = getMenuItemImage(imageUrl, category, menuItemId, menuItemName);

  const handleIncrement = () => {
    updateQuantity(cartItemId, quantity + 1);
  };

  const handleDecrement = () => {
    updateQuantity(cartItemId, quantity - 1);
  };

  const handleRemove = () => {
    removeCartItem(cartItemId);
  };

  return (
    <div className="py-4 sm:py-5 border-b border-gray-100 last:border-b-0 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
      
      {/* Left: Image & Name */}
      <div className="flex items-center space-x-3.5 flex-1 min-w-0">
        <div className="w-16 h-16 rounded-xl overflow-hidden bg-gray-100 flex-shrink-0 border border-gray-200/60 shadow-inner">
          <img
            src={displayImage}
            alt={menuItemName}
            className="w-full h-full object-cover"
            loading="lazy"
          />
        </div>

        <div className="truncate">
          <h4 className="text-base font-bold text-gray-900 truncate">
            {menuItemName}
          </h4>
          <div className="flex items-center space-x-2 mt-0.5">
            {category && (
              <span className="text-xs font-medium text-gray-400 bg-gray-100 px-2 py-0.5 rounded-full">
                {category}
              </span>
            )}
            <span className="text-xs text-gray-500 font-semibold">
              {formatPrice(price)} each
            </span>
          </div>
        </div>
      </div>

      {/* Right: Quantity Controls & Subtotal */}
      <div className="flex items-center justify-between w-full sm:w-auto space-x-4 self-end sm:self-center">
        
        {/* Quantity Controls Pill */}
        <div className="flex items-center border border-gray-200 rounded-xl bg-white shadow-sm overflow-hidden">
          <button
            type="button"
            onClick={handleDecrement}
            disabled={loading}
            className="p-1.5 sm:p-2 text-gray-600 hover:text-brand-600 hover:bg-orange-50 disabled:opacity-40 transition"
            title="Decrease quantity"
          >
            <Minus className="w-3.5 h-3.5" />
          </button>

          <span className="px-3 text-sm font-bold text-gray-800 min-w-[2rem] text-center">
            {quantity}
          </span>

          <button
            type="button"
            onClick={handleIncrement}
            disabled={loading}
            className="p-1.5 sm:p-2 text-gray-600 hover:text-brand-600 hover:bg-orange-50 disabled:opacity-40 transition"
            title="Increase quantity"
          >
            <Plus className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Item Subtotal */}
        <div className="text-right min-w-[5rem]">
          <span className="text-base font-extrabold text-gray-900 block">
            {formatPrice(subTotal)}
          </span>
        </div>

        {/* Delete Item Button */}
        <button
          type="button"
          onClick={handleRemove}
          disabled={loading}
          className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-xl transition"
          title="Remove item from cart"
        >
          <Trash2 className="w-4 h-4" />
        </button>

      </div>

    </div>
  );
};

export default CartItemRow;

