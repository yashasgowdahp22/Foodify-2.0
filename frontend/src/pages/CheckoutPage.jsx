import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { orderApi } from '../api/orderApi';
import { formatPrice, getMenuItemImage } from '../utils/constants';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';
import {
  MapPin,
  FileText,
  CreditCard,
  Banknote,
  QrCode,
  ShieldCheck,
  ShoppingBag,
  ArrowRight,
  Store,
  Info
} from 'lucide-react';

/**
 * CheckoutPage Component
 * 
 * Handles Foodify 2.0 customer order placement:
 * 1. Delivery address configuration (prefilled from profile, editable for order).
 * 2. Special cooking and delivery instructions.
 * 3. Payment method selection (COD, UPI, CARD with simulated instant verification).
 * 4. Itemized order summary with transparent bill details.
 * 5. Calls Spring Boot POST /checkout, clears cart upon success, and redirects to Order Confirmation.
 */
const CheckoutPage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { cart, loading: cartLoading, fetchCart } = useCart();

  // Form states
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [specialInstructions, setSpecialInstructions] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('COD'); // 'COD' | 'UPI' | 'CARD'

  // Submission & error states
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // Initialize delivery address from customer profile once loaded
  useEffect(() => {
    if (user?.address && !deliveryAddress) {
      setDeliveryAddress(user.address);
    }
  }, [user]);

  // Pricing calculations
  const subtotal = cart?.totalAmount || 0;
  const deliveryFee = subtotal >= 200 || subtotal === 0 ? 0 : 40;
  const platformFee = 5;
  const grandTotal = subtotal + deliveryFee + platformFee;

  // Handle Order Placement
  const handlePlaceOrder = async (e) => {
    e.preventDefault();

    // Validations
    if (!cart?.items || cart.items.length === 0) {
      setError('Your cart is empty. Please add items before placing an order.');
      return;
    }

    if (!deliveryAddress.trim()) {
      setError('Please provide a valid delivery address to complete your order.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      const requestPayload = {
        deliveryAddress: deliveryAddress.trim(),
        specialInstructions: specialInstructions.trim(),
        paymentMethod: paymentMethod, // 'COD', 'UPI', or 'CARD'
      };

      // Call backend POST /checkout
      const orderResponse = await orderApi.checkout(requestPayload);

      // Refresh cart context (backend clears database cart items on checkout)
      await fetchCart();

      // Navigate to order confirmation page
      navigate(`/order-success/${orderResponse.id}`, {
        state: { order: orderResponse },
        replace: true,
      });
    } catch (err) {
      console.error('Order placement failed:', err);
      const serverMessage =
        err.response?.data?.message ||
        (typeof err.response?.data === 'string' ? err.response.data : null) ||
        'Unable to place your order. Please verify restaurant availability and try again.';
      setError(serverMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  // If cart is loading
  if (cartLoading && (!cart || !cart.items)) {
    return (
      <div className="py-20 flex justify-center">
        <LoadingSpinner text="Preparing your checkout..." />
      </div>
    );
  }

  // If cart is empty, show empty state
  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-16 text-center">
        <div className="w-20 h-20 bg-orange-50 text-brand-500 rounded-3xl mx-auto flex items-center justify-center mb-6 shadow-sm border border-orange-100">
          <ShoppingBag className="w-10 h-10" />
        </div>
        <h2 className="text-2xl font-black text-gray-900 tracking-tight">Your Cart is Empty</h2>
        <p className="mt-2 text-sm text-gray-600 max-w-md mx-auto">
          You don't have any items in your cart to checkout. Browse our top restaurants and add your favorite dishes!
        </p>
        <div className="mt-6 flex justify-center space-x-3">
          <Link
            to="/"
            className="px-6 py-3 rounded-2xl bg-brand-500 text-white font-bold text-sm hover:bg-brand-600 transition shadow-md shadow-brand-500/20"
          >
            Explore Restaurants
          </Link>
          <Link
            to="/cart"
            className="px-6 py-3 rounded-2xl bg-white border border-gray-200 text-gray-700 font-bold text-sm hover:bg-gray-50 transition"
          >
            View Cart
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-10">
      
      {/* Page Title & Breadcrumb */}
      <div className="mb-6">
        <div className="flex items-center space-x-2 text-xs font-semibold text-gray-500 mb-2">
          <Link to="/" className="hover:text-brand-600">Home</Link>
          <span>/</span>
          <Link to="/cart" className="hover:text-brand-600">Cart</Link>
          <span>/</span>
          <span className="text-brand-600 font-bold">Checkout</span>
        </div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
          Checkout &amp; Order Placement
        </h1>
        <p className="mt-1 text-sm text-gray-500">
          Review your delivery details, payment method, and complete your order.
        </p>
      </div>

      {/* Error Alert */}
      {error && (
        <div className="mb-6">
          <ErrorAlert message={error} onClose={() => setError(null)} />
        </div>
      )}

      {/* Main Two-Column Layout */}
      <form onSubmit={handlePlaceOrder} className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        {/* Left Column (8 cols): Delivery, Instructions, Payment */}
        <div className="lg:col-span-7 space-y-6">

          {/* Section 1: Delivery Address */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <div className="flex items-center space-x-3 pb-4 border-b border-gray-100">
              <div className="w-10 h-10 rounded-2xl bg-orange-50 text-brand-600 flex items-center justify-center flex-shrink-0 font-bold">
                <MapPin className="w-5 h-5" />
              </div>
              <div>
                <h2 className="text-base font-bold text-gray-900">1. Delivery Address</h2>
                <p className="text-xs text-gray-500">Where should we deliver your hot &amp; fresh food?</p>
              </div>
            </div>

            <div className="mt-5 space-y-3">
              <div>
                <label htmlFor="deliveryAddress" className="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-1.5">
                  Complete Address <span className="text-red-500">*</span>
                </label>
                <textarea
                  id="deliveryAddress"
                  rows={3}
                  value={deliveryAddress}
                  onChange={(e) => setDeliveryAddress(e.target.value)}
                  placeholder="Flat/House No., Building Name, Street, Landmark, City, Pincode"
                  required
                  className="w-full px-4 py-3 rounded-2xl border border-gray-200 text-sm text-gray-800 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 transition resize-none placeholder-gray-400"
                />
              </div>

              <div className="flex items-center justify-between text-xs text-gray-500 pt-1">
                <span>
                  {user?.address ? '✓ Loaded from your Foodify profile' : 'Tip: Add an address here for this delivery'}
                </span>
                <Link to="/profile" className="font-semibold text-brand-600 hover:text-brand-700 underline">
                  Update default in Profile
                </Link>
              </div>
            </div>
          </div>

          {/* Section 2: Special Delivery & Cooking Instructions */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <div className="flex items-center space-x-3 pb-4 border-b border-gray-100">
              <div className="w-10 h-10 rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center flex-shrink-0 font-bold">
                <FileText className="w-5 h-5" />
              </div>
              <div>
                <h2 className="text-base font-bold text-gray-900">2. Delivery Instructions</h2>
                <p className="text-xs text-gray-500">Special requests for the restaurant or delivery partner (optional)</p>
              </div>
            </div>

            <div className="mt-5">
              <label htmlFor="specialInstructions" className="block text-xs font-bold uppercase tracking-wider text-gray-700 mb-1.5">
                Notes for Rider &amp; Kitchen
              </label>
              <textarea
                id="specialInstructions"
                rows={2}
                value={specialInstructions}
                onChange={(e) => setSpecialInstructions(e.target.value)}
                placeholder="e.g. Please do not ring the doorbell, leave package with security guard, call upon arrival..."
                className="w-full px-4 py-3 rounded-2xl border border-gray-200 text-sm text-gray-800 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 transition resize-none placeholder-gray-400"
              />
            </div>
          </div>

          {/* Section 3: Payment Method Selection */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <div className="flex items-center space-x-3 pb-4 border-b border-gray-100">
              <div className="w-10 h-10 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center flex-shrink-0 font-bold">
                <CreditCard className="w-5 h-5" />
              </div>
              <div>
                <h2 className="text-base font-bold text-gray-900">3. Payment Method</h2>
                <p className="text-xs text-gray-500">Select how you want to settle the bill</p>
              </div>
            </div>

            <div className="mt-5 space-y-3">
              
              {/* Option 1: Cash on Delivery (COD) */}
              <label
                className={`relative flex items-start p-4 rounded-2xl border cursor-pointer transition ${
                  paymentMethod === 'COD'
                    ? 'border-brand-500 bg-orange-50/40 ring-1 ring-brand-500'
                    : 'border-gray-200 hover:border-gray-300 bg-white'
                }`}
              >
                <input
                  type="radio"
                  name="paymentMethod"
                  value="COD"
                  checked={paymentMethod === 'COD'}
                  onChange={() => setPaymentMethod('COD')}
                  className="mt-1 h-4 w-4 text-brand-600 focus:ring-brand-500 border-gray-300"
                />
                <div className="ml-3.5 flex-1">
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-bold text-gray-900 flex items-center space-x-2">
                      <Banknote className="w-4 h-4 text-emerald-600" />
                      <span>Cash on Delivery (COD)</span>
                    </span>
                    <span className="text-[11px] font-bold text-amber-700 bg-amber-100 px-2 py-0.5 rounded-full">
                      Pay on Delivery
                    </span>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">
                    Pay with cash or UPI directly to our delivery partner when your order reaches your doorstep.
                  </p>
                </div>
              </label>

              {/* Option 2: UPI (Simulated) */}
              <label
                className={`relative flex items-start p-4 rounded-2xl border cursor-pointer transition ${
                  paymentMethod === 'UPI'
                    ? 'border-brand-500 bg-orange-50/40 ring-1 ring-brand-500'
                    : 'border-gray-200 hover:border-gray-300 bg-white'
                }`}
              >
                <input
                  type="radio"
                  name="paymentMethod"
                  value="UPI"
                  checked={paymentMethod === 'UPI'}
                  onChange={() => setPaymentMethod('UPI')}
                  className="mt-1 h-4 w-4 text-brand-600 focus:ring-brand-500 border-gray-300"
                />
                <div className="ml-3.5 flex-1">
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-bold text-gray-900 flex items-center space-x-2">
                      <QrCode className="w-4 h-4 text-brand-600" />
                      <span>UPI Instant Pay (Simulated)</span>
                    </span>
                    <span className="text-[11px] font-bold text-emerald-700 bg-emerald-100 px-2 py-0.5 rounded-full">
                      Instant Success
                    </span>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">
                    Google Pay, PhonePe, Paytm, BHIM, or any UPI App (Simulated instant transaction).
                  </p>
                </div>
              </label>

              {/* Option 3: Credit / Debit Card (Simulated) */}
              <label
                className={`relative flex items-start p-4 rounded-2xl border cursor-pointer transition ${
                  paymentMethod === 'CARD'
                    ? 'border-brand-500 bg-orange-50/40 ring-1 ring-brand-500'
                    : 'border-gray-200 hover:border-gray-300 bg-white'
                }`}
              >
                <input
                  type="radio"
                  name="paymentMethod"
                  value="CARD"
                  checked={paymentMethod === 'CARD'}
                  onChange={() => setPaymentMethod('CARD')}
                  className="mt-1 h-4 w-4 text-brand-600 focus:ring-brand-500 border-gray-300"
                />
                <div className="ml-3.5 flex-1">
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-bold text-gray-900 flex items-center space-x-2">
                      <CreditCard className="w-4 h-4 text-blue-600" />
                      <span>Credit / Debit Card (Simulated)</span>
                    </span>
                    <span className="text-[11px] font-bold text-blue-700 bg-blue-100 px-2 py-0.5 rounded-full">
                      Instant Success
                    </span>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">
                    Visa, MasterCard, RuPay cards accepted (Simulated instant transaction).
                  </p>
                </div>
              </label>

            </div>

            {/* Simulated Payment Notice Banner */}
            <div className="mt-4 p-3.5 rounded-2xl bg-amber-50/80 border border-amber-200 text-xs text-amber-900 flex items-start space-x-2.5">
              <Info className="w-4 h-4 text-amber-600 flex-shrink-0 mt-0.5" />
              <div>
                <p className="font-bold">Test Sandbox Payment Notice</p>
                <p className="mt-0.5 text-amber-800">
                  Foodify 2.0 uses a simulated payment service in the backend. When choosing <strong>UPI</strong> or <strong>CARD</strong>, your payment is immediately approved with a generated transaction ID (e.g. <code>TXN-XXXXXXXXXX</code>). No actual money is debited.
                </p>
              </div>
            </div>

          </div>

        </div>

        {/* Right Column (5 cols): Order Summary, Bill, Submit */}
        <div className="lg:col-span-5 space-y-6">

          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm sticky top-24">
            
            {/* Restaurant Heading */}
            <div className="flex items-center space-x-3 pb-4 border-b border-gray-100">
              <div className="w-10 h-10 rounded-2xl bg-orange-100/70 text-brand-600 flex items-center justify-center flex-shrink-0">
                <Store className="w-5 h-5" />
              </div>
              <div className="overflow-hidden">
                <h3 className="text-base font-extrabold text-gray-900 truncate">
                  {cart.restaurantName || 'Restaurant'}
                </h3>
                <p className="text-xs text-gray-500">
                  {cart.items.length} {cart.items.length === 1 ? 'item' : 'items'} in order
                </p>
              </div>
            </div>

            {/* Itemized List */}
            <div className="py-4 space-y-3 max-h-64 overflow-y-auto pr-1 border-b border-gray-100">
              {cart.items.map((item) => (
                <div key={item.cartItemId} className="flex items-center justify-between text-xs gap-3">
                  <div className="flex items-center space-x-2 min-w-0 flex-1">
                    <img
                      src={getMenuItemImage(item.menuItemId, item.menuItemName)}
                      alt={item.menuItemName}
                      className="w-10 h-10 rounded-xl object-cover flex-shrink-0 border border-gray-100"
                    />
                    <div className="truncate">
                      <p className="font-bold text-gray-900 truncate">{item.menuItemName}</p>
                      <p className="text-gray-500">
                        {formatPrice(item.price)} × {item.quantity}
                      </p>
                    </div>
                  </div>
                  <span className="font-extrabold text-gray-900 flex-shrink-0">
                    {formatPrice(item.subTotal)}
                  </span>
                </div>
              ))}
            </div>

            {/* Bill Details */}
            <div className="py-4 space-y-2.5 text-xs border-b border-gray-100">
              <div className="flex items-center justify-between text-gray-600">
                <span>Item Subtotal</span>
                <span className="font-bold text-gray-900">{formatPrice(subtotal)}</span>
              </div>

              <div className="flex items-center justify-between text-gray-600">
                <div className="flex items-center space-x-1">
                  <span>Delivery Partner Fee</span>
                  {deliveryFee === 0 && (
                    <span className="text-[10px] font-bold uppercase tracking-wider text-emerald-700 bg-emerald-100 px-1.5 py-0.5 rounded">
                      Free
                    </span>
                  )}
                </div>
                <span className="font-bold text-gray-900">
                  {deliveryFee === 0 ? 'FREE' : formatPrice(deliveryFee)}
                </span>
              </div>

              <div className="flex items-center justify-between text-gray-600">
                <span>Platform Fee</span>
                <span className="font-bold text-gray-900">{formatPrice(platformFee)}</span>
              </div>
            </div>

            {/* Grand Total */}
            <div className="pt-4 pb-6 flex items-center justify-between">
              <div>
                <span className="text-xs uppercase tracking-wider font-extrabold text-gray-500 block">
                  Total Payable
                </span>
                <span className="text-2xl font-black text-brand-600">
                  {formatPrice(grandTotal)}
                </span>
              </div>
              <span className="text-[11px] font-semibold text-gray-500 bg-gray-100 px-2.5 py-1 rounded-full">
                Inclusive of taxes
              </span>
            </div>

            {/* Place Order CTA Button */}
            <button
              type="submit"
              disabled={isSubmitting || cart.itemCount === 0}
              className={`w-full py-4 rounded-2xl font-extrabold text-sm text-white shadow-lg transition flex items-center justify-center space-x-2 ${
                isSubmitting || cart.itemCount === 0
                  ? 'bg-gray-400 cursor-not-allowed shadow-none'
                  : 'bg-brand-500 hover:bg-brand-600 shadow-brand-500/25 active:scale-[0.99]'
              }`}
            >
              {isSubmitting ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                  <span>Placing Your Order...</span>
                </>
              ) : (
                <>
                  <span>Place Order • {formatPrice(grandTotal)}</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>

            {/* Safety & Trust Note */}
            <div className="mt-4 p-3 bg-gray-50 rounded-2xl border border-gray-100 flex items-center space-x-2 text-[11px] text-gray-500">
              <ShieldCheck className="w-4 h-4 text-emerald-600 flex-shrink-0" />
              <span>Safe &amp; contactless delivery. Fresh food prepared on demand.</span>
            </div>

          </div>

        </div>

      </form>

    </div>
  );
};

export default CheckoutPage;

