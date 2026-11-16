import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import CartItemRow from '../components/cart/CartItemRow';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import ErrorAlert from '../components/common/ErrorAlert';
import { formatPrice } from '../utils/constants';
import { 
  ShoppingBag, 
  Trash2, 
  Store, 
  MapPin, 
  ArrowRight, 
  ShieldCheck, 
  Receipt 
} from 'lucide-react';

/**
 * Customer Cart Page
 * 
 * Features:
 * 1. Displays current items in customer's cart fetched from GET /myCart.
 * 2. Item details: dish photo, name, category, price, quantity controls, and subtotal.
 * 3. Total items and total bill breakdown (subtotal, delivery fee, platform fee, grand total).
 * 4. Clear entire cart action (DELETE /myCart).
 * 5. Delivery address preview linked to profile.
 * 6. Empty cart and loading states.
 */
const CartPage = () => {
  const navigate = useNavigate();
  const { cart, loading, error, clearCart } = useCart();
  const { user } = useAuth();
  const [clearConfirmOpen, setClearConfirmOpen] = useState(false);

  const hasItems = cart && cart.itemCount > 0 && cart.items && cart.items.length > 0;

  // Consistent bill calculations across Cart and Checkout
  const subtotal = Number(cart?.totalAmount) || 0;
  const deliveryFee = subtotal >= 200 || subtotal === 0 ? 0 : 40;
  const platformFee = subtotal > 0 ? 5 : 0;
  const grandTotal = subtotal + deliveryFee + platformFee;

  const handleClearCart = async () => {
    await clearCart();
    setClearConfirmOpen(false);
  };

  if (loading && (!cart || cart.items.length === 0)) {
    return (
      <div className="min-h-screen py-16">
        <LoadingSpinner message="Loading your delicious food cart..." />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 min-h-screen">
      
      {/* Page Title */}
      <div className="flex items-center justify-between pb-6 mb-8 border-b border-gray-200">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight flex items-center space-x-2">
            <ShoppingBag className="w-7 h-7 text-brand-500" />
            <span>My Food Cart</span>
          </h1>
          <p className="text-sm text-gray-500 mt-1">
            Review your selected dishes before placing your order.
          </p>
        </div>

        {hasItems && (
          <span className="text-xs font-bold text-brand-700 bg-orange-100 px-3.5 py-1.5 rounded-full">
            {cart.itemCount} {cart.itemCount === 1 ? 'Item' : 'Items'}
          </span>
        )}
      </div>

      {/* Error Alert */}
      {error && <ErrorAlert message={error} />}

      {/* Empty Cart State */}
      {!hasItems && (
        <EmptyState
          icon={ShoppingBag}
          title="Your Cart is Empty"
          description="Looks like you haven't added anything to your cart yet. Explore top restaurants near you and pick something delicious!"
          actionText="Browse Restaurants"
          onAction={() => navigate('/')}
        />
      )}

      {/* Active Cart Layout */}
      {hasItems && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
          
          {/* Left Column: Items List */}
          <div className="lg:col-span-2 space-y-6">
            
            {/* Restaurant Info Card */}
            <div className="bg-white rounded-3xl p-5 sm:p-6 border border-gray-200/80 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div className="flex items-center space-x-3.5">
                <div className="w-12 h-12 rounded-2xl bg-orange-100 text-brand-600 flex items-center justify-center flex-shrink-0">
                  <Store className="w-6 h-6" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-gray-900">
                    {cart.restaurantName || 'Restaurant'}
                  </h3>
                  <Link
                    to={`/restaurant/${cart.restaurantId}`}
                    className="text-xs font-semibold text-brand-600 hover:text-brand-700 inline-flex items-center mt-0.5"
                  >
                    <span>+ Add more dishes from this restaurant</span>
                  </Link>
                </div>
              </div>

              {/* Clear Cart Button */}
              {clearConfirmOpen ? (
                <div className="flex items-center space-x-2 self-end sm:self-center">
                  <span className="text-xs font-semibold text-gray-500">Confirm?</span>
                  <button
                    onClick={handleClearCart}
                    className="px-3 py-1.5 rounded-lg bg-red-600 text-white text-xs font-bold hover:bg-red-700 transition"
                  >
                    Yes, Clear
                  </button>
                  <button
                    onClick={() => setClearConfirmOpen(false)}
                    className="px-3 py-1.5 rounded-lg bg-gray-200 text-gray-700 text-xs font-bold hover:bg-gray-300 transition"
                  >
                    Cancel
                  </button>
                </div>
              ) : (
                <button
                  onClick={() => setClearConfirmOpen(true)}
                  className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold text-gray-500 hover:text-red-600 hover:bg-red-50 border border-gray-200 transition self-end sm:self-center"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                  <span>Clear Cart</span>
                </button>
              )}
            </div>

            {/* Cart Items Card */}
            <div className="bg-white rounded-3xl p-5 sm:p-6 border border-gray-200/80 shadow-sm">
              <h3 className="text-base font-bold text-gray-900 pb-4 border-b border-gray-100">
                Order Items ({cart.items.length})
              </h3>
              <div>
                {cart.items.map((item) => (
                  <CartItemRow key={item.cartItemId} item={item} />
                ))}
              </div>
            </div>

            {/* Delivery Address Preview */}
            <div className="bg-white rounded-3xl p-5 sm:p-6 border border-gray-200/80 shadow-sm">
              <div className="flex items-start justify-between gap-4">
                <div className="flex items-start space-x-3">
                  <div className="w-10 h-10 rounded-xl bg-orange-50 text-brand-600 flex items-center justify-center flex-shrink-0 mt-0.5">
                    <MapPin className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-gray-900">Delivery Address</h4>
                    <p className="text-xs text-gray-600 mt-1 max-w-md">
                      {user?.address || 'No address set in profile. Please update your profile.'}
                    </p>
                  </div>
                </div>
                <Link
                  to="/profile"
                  className="text-xs font-bold text-brand-600 hover:text-brand-700 underline flex-shrink-0"
                >
                  Change
                </Link>
              </div>
            </div>

          </div>

          {/* Right Column: Bill Details & Summary */}
          <div className="space-y-6">
            
            <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
              <h3 className="text-lg font-bold text-gray-900 pb-4 border-b border-gray-100 flex items-center space-x-2">
                <Receipt className="w-5 h-5 text-brand-500" />
                <span>Bill Details</span>
              </h3>

              <div className="py-4 space-y-3 text-sm border-b border-gray-100">
                
                {/* Item Subtotal */}
                <div className="flex items-center justify-between text-gray-600">
                  <span>Item Subtotal</span>
                  <span className="font-semibold text-gray-900">{formatPrice(subtotal)}</span>
                </div>

                {/* Delivery Fee */}
                <div className="flex items-center justify-between text-gray-600">
                  <div className="flex items-center space-x-1">
                    <span>Delivery Partner Fee</span>
                    {deliveryFee === 0 && (
                      <span className="text-[10px] font-bold uppercase tracking-wider text-emerald-700 bg-emerald-100 px-1.5 py-0.5 rounded">
                        Free
                      </span>
                    )}
                  </div>
                  <span className="font-semibold text-gray-900">
                    {deliveryFee === 0 ? 'FREE' : formatPrice(deliveryFee)}
                  </span>
                </div>

                {/* Platform Fee */}
                <div className="flex items-center justify-between text-gray-600">
                  <span>Platform &amp; Handling</span>
                  <span className="font-semibold text-gray-900">{formatPrice(platformFee)}</span>
                </div>

              </div>

              {/* Grand Total */}
              <div className="pt-4 pb-6 flex items-center justify-between text-base font-extrabold text-gray-900">
                <span>To Pay</span>
                <span className="text-xl text-brand-600">{formatPrice(grandTotal)}</span>
              </div>

              {/* Proceed to Checkout Button */}
              <div className="space-y-3">
                <Link
                  to="/checkout"
                  className="w-full py-3.5 rounded-2xl bg-brand-500 text-white font-bold text-sm shadow-md shadow-brand-500/20 hover:bg-brand-600 active:scale-[0.99] transition flex items-center justify-center space-x-2"
                >
                  <span>Proceed to Checkout</span>
                  <ArrowRight className="w-4 h-4" />
                </Link>
              </div>

            </div>

            {/* Safety Guarantee */}
            <div className="p-4 rounded-2xl bg-gray-50 border border-gray-200/70 text-xs text-gray-500 flex items-center space-x-3">
              <ShieldCheck className="w-5 h-5 text-emerald-600 flex-shrink-0" />
              <span>Safe and hygienic food delivery guaranteed by Foodify 2.0 verified partners.</span>
            </div>

          </div>

        </div>
      )}

    </div>
  );
};

export default CartPage;

