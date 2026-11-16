import React from 'react';
import { useCart } from '../../context/CartContext';
import { AlertTriangle, Trash2 } from 'lucide-react';

/**
 * One-Restaurant-Per-Cart Conflict Modal
 * Triggered whenever a customer tries to add items from Restaurant B
 * while their cart contains items from Restaurant A.
 */
const RestaurantConflictModal = () => {
  const { conflictModal, confirmReplaceCart, closeConflictModal, loading } = useCart();

  if (!conflictModal.isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="bg-white rounded-3xl max-w-md w-full p-6 sm:p-8 shadow-2xl border border-gray-100 transform animate-in zoom-in-95 duration-200">
        
        {/* Warning Icon */}
        <div className="w-14 h-14 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center mx-auto mb-5">
          <AlertTriangle className="w-7 h-7" />
        </div>

        {/* Modal Title */}
        <h3 className="text-xl font-bold text-gray-900 text-center tracking-tight">
          Replace items in cart?
        </h3>

        {/* Explanation */}
        <p className="mt-3 text-sm text-gray-600 text-center leading-relaxed">
          Your cart already contains dishes from <strong className="text-gray-900 font-semibold">{conflictModal.existingRestaurantName}</strong>.
          <br /><br />
          Foodify allows ordering from only <strong>one restaurant at a time</strong>. Would you like to clear your existing cart and start a new order from <strong className="text-brand-600 font-semibold">{conflictModal.newRestaurantName}</strong>?
        </p>

        {/* Action Buttons */}
        <div className="mt-8 flex flex-col-reverse sm:flex-row items-center justify-end gap-3">
          <button
            type="button"
            onClick={closeConflictModal}
            disabled={loading}
            className="w-full sm:w-auto px-5 py-2.5 rounded-xl border border-gray-300 text-gray-700 hover:bg-gray-50 text-sm font-semibold transition"
          >
            Keep Existing Cart
          </button>

          <button
            type="button"
            onClick={confirmReplaceCart}
            disabled={loading}
            className="w-full sm:w-auto px-5 py-2.5 rounded-xl bg-brand-500 hover:bg-brand-600 disabled:opacity-50 text-white text-sm font-semibold shadow-md shadow-brand-500/20 transition flex items-center justify-center space-x-2"
          >
            {loading ? (
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
            ) : (
              <>
                <Trash2 className="w-4 h-4" />
                <span>Clear &amp; Add Item</span>
              </>
            )}
          </button>
        </div>

      </div>
    </div>
  );
};

export default RestaurantConflictModal;

