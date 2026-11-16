import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import { formatPrice, getMenuItemImage } from '../utils/constants';
import LoadingSpinner from '../components/common/LoadingSpinner';
import OrderStatusBadge from '../components/order/OrderStatusBadge';
import OrderStatusTracker from '../components/order/OrderStatusTracker';
import CancelOrderModal from '../components/order/CancelOrderModal';
import {
  ArrowLeft,
  Calendar,
  Store,
  MapPin,
  CreditCard,
  Banknote,
  QrCode,
  FileText,
  User,
  ShieldCheck,
  Receipt,
  Truck,
  CheckCircle2,
  AlertTriangle,
  ExternalLink
} from 'lucide-react';

/**
 * OrderDetailPage Component
 * 
 * Detailed receipt and status tracking for a specific customer order:
 * - Fetches order by ID via GET /myOrders/{orderId}
 * - Visual status stepper (PENDING -> CONFIRMED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED)
 * - Detailed restaurant and itemized dish receipts with unit prices and subtotals.
 * - Delivery destination address and special instructions.
 * - Payment breakdown with method, status, and simulated transaction ID.
 * - Order cancellation for PENDING orders with real-time state update.
 */
const OrderDetailPage = () => {
  const { orderId } = useParams();

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successBanner, setSuccessBanner] = useState(null);

  // Cancellation modal state
  const [cancelModalOpen, setCancelModalOpen] = useState(false);

  // Fetch order details from backend
  const fetchOrder = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await orderApi.getMyOrderById(orderId);
      setOrder(data);
    } catch (err) {
      console.error('Failed to load order details:', err);
      const msg =
        err.response?.data?.message ||
        `Could not retrieve order #${orderId}. Please verify your access.`;
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (orderId) {
      fetchOrder();
    }
  }, [orderId]);

  // Handle cancellation execution
  const handleCancelSuccess = async (id) => {
    const updated = await orderApi.cancelMyOrder(id);
    setOrder(updated);
    setSuccessBanner('Your order has been cancelled successfully.');
    setTimeout(() => setSuccessBanner(null), 6000);
  };

  if (loading) {
    return (
      <div className="py-24 flex justify-center">
        <LoadingSpinner text={`Loading details for order #${orderId}...`} />
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="max-w-xl mx-auto px-4 py-16 text-center">
        <div className="w-16 h-16 bg-red-50 text-red-500 rounded-3xl mx-auto flex items-center justify-center mb-4 border border-red-100">
          <AlertTriangle className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-black text-gray-900 tracking-tight">Order Not Found</h2>
        <p className="mt-2 text-sm text-gray-600">
          {error || `Unable to load details for order #${orderId}.`}
        </p>
        <div className="mt-6 flex justify-center space-x-3">
          <Link
            to="/orders"
            className="px-6 py-2.5 rounded-xl bg-brand-500 text-white font-bold text-xs hover:bg-brand-600 transition shadow-md shadow-brand-500/20"
          >
            Back to My Orders
          </Link>
          <Link
            to="/"
            className="px-6 py-2.5 rounded-xl bg-white border border-gray-200 text-gray-700 font-bold text-xs hover:bg-gray-50 transition"
          >
            Home
          </Link>
        </div>
      </div>
    );
  }

  const isPending = (order.status || '').toUpperCase() === 'PENDING';
  const isCancelled = (order.status || '').toUpperCase() === 'CANCELLED';
  const paymentMethodUpper = (order.paymentMethod || '').toUpperCase();
  const isCod = paymentMethodUpper === 'COD';
  const isPaid = (order.paymentStatus || '').toUpperCase() === 'SUCCESS';
  const isRefunded = (order.paymentStatus || '').toUpperCase() === 'REFUNDED';

  const formattedDate = order.orderDate
    ? new Date(order.orderDate).toLocaleString('en-IN', {
        dateStyle: 'full',
        timeStyle: 'medium',
      })
    : 'Date unavailable';

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-10">
      
      {/* Top Breadcrumb & Back Navigation */}
      <div className="flex items-center justify-between gap-4 mb-6">
        <div className="flex items-center space-x-2 text-xs font-semibold text-gray-500">
          <Link to="/" className="hover:text-brand-600">Home</Link>
          <span>/</span>
          <Link to="/orders" className="hover:text-brand-600">My Orders</Link>
          <span>/</span>
          <span className="text-brand-600 font-bold">Order #{order.id}</span>
        </div>

        <Link
          to="/orders"
          className="inline-flex items-center space-x-1.5 text-xs font-bold text-gray-600 hover:text-brand-600 transition"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Back to Orders</span>
        </Link>
      </div>

      {/* Success Notification Banner */}
      {successBanner && (
        <div className="mb-6 p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-900 text-sm flex items-center justify-between">
          <div className="flex items-center space-x-2.5">
            <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
            <span className="font-semibold">{successBanner}</span>
          </div>
          <button
            onClick={() => setSuccessBanner(null)}
            className="text-xs font-bold text-emerald-700 hover:underline ml-4"
          >
            Dismiss
          </button>
        </div>
      )}

      {/* Main Order Header Bar */}
      <div className="bg-white rounded-3xl p-6 sm:p-7 border border-gray-200/80 shadow-sm mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-3 mb-1">
            <h1 className="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
              Order #{order.id}
            </h1>
            <OrderStatusBadge status={order.status} size="lg" />
          </div>
          <p className="text-xs text-gray-500 flex items-center space-x-1.5">
            <Calendar className="w-3.5 h-3.5" />
            <span>Placed on {formattedDate}</span>
          </p>
        </div>

        {/* Action: Cancel Order (Only if PENDING) */}
        {isPending && (
          <button
            type="button"
            onClick={() => setCancelModalOpen(true)}
            className="px-5 py-2.5 rounded-xl border border-rose-200 bg-rose-50 hover:bg-rose-100 text-rose-700 font-bold text-xs transition shadow-sm active:scale-[0.99]"
          >
            Cancel This Order
          </button>
        )}
      </div>

      {/* Visual Order Progress Tracker */}
      <div className="mb-8">
        <OrderStatusTracker status={order.status} />
      </div>

      {/* Two-Column Detail Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        {/* Left Column (7 cols): Restaurant, Ordered Dishes, Bill Details */}
        <div className="lg:col-span-7 space-y-6">

          {/* Restaurant Header Card */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3.5">
                <div className="w-12 h-12 rounded-2xl bg-orange-100/70 text-brand-600 flex items-center justify-center flex-shrink-0">
                  <Store className="w-6 h-6" />
                </div>
                <div>
                  <span className="text-[11px] font-bold uppercase tracking-wider text-gray-400 block">
                    Ordered From
                  </span>
                  <h3 className="text-lg font-black text-gray-900 mt-0.5">
                    {order.restaurantName || 'Restaurant'}
                  </h3>
                </div>
              </div>

              {order.restaurantId && (
                <Link
                  to={`/restaurant/${order.restaurantId}`}
                  className="inline-flex items-center space-x-1 text-xs font-bold text-brand-600 hover:text-brand-700 hover:underline"
                >
                  <span>View Menu</span>
                  <ExternalLink className="w-3.5 h-3.5" />
                </Link>
              )}
            </div>
          </div>

          {/* Itemized Food Items Card */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <h3 className="text-sm font-bold uppercase tracking-wider text-gray-700 pb-4 border-b border-gray-100 flex items-center space-x-2">
              <Receipt className="w-4 h-4 text-brand-500" />
              <span>Ordered Dishes ({order.items?.length || 0})</span>
            </h3>

            <div className="divide-y divide-gray-100">
              {order.items && order.items.length > 0 ? (
                order.items.map((item, idx) => (
                  <div
                    key={item.id || idx}
                    className="py-4 flex items-center justify-between text-sm gap-3"
                  >
                    <div className="flex items-center space-x-3.5 min-w-0">
                      <img
                        src={getMenuItemImage(item.menuItemId, item.menuItemName)}
                        alt={item.menuItemName}
                        className="w-14 h-14 rounded-2xl object-cover flex-shrink-0 border border-gray-100"
                      />
                      <div className="truncate">
                        <p className="font-extrabold text-gray-900 truncate">
                          {item.menuItemName}
                        </p>
                        <p className="text-xs text-gray-500 mt-0.5">
                          {formatPrice(item.price)} × {item.quantity}
                        </p>
                      </div>
                    </div>
                    <span className="font-black text-gray-900 flex-shrink-0">
                      {formatPrice(item.subTotal)}
                    </span>
                  </div>
                ))
              ) : (
                <p className="py-6 text-xs text-gray-400 text-center">
                  No order item records returned for this order.
                </p>
              )}
            </div>
          </div>

          {/* Bill Summary Breakdown */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <h3 className="text-sm font-bold uppercase tracking-wider text-gray-700 pb-3 border-b border-gray-100">
              Payment &amp; Bill Breakdown
            </h3>

            <div className="py-4 space-y-3 text-xs border-b border-gray-100">
              <div className="flex items-center justify-between text-gray-600">
                <span>Items Subtotal</span>
                <span className="font-bold text-gray-900">{formatPrice(order.totalAmount)}</span>
              </div>

              <div className="flex items-center justify-between text-gray-600">
                <span>Delivery Partner Fee</span>
                <span className="font-bold text-emerald-600">FREE</span>
              </div>

              <div className="flex items-center justify-between text-gray-600">
                <span>Platform Fee &amp; Taxes</span>
                <span className="font-bold text-gray-900">Included</span>
              </div>
            </div>

            <div className="pt-4 flex items-center justify-between text-base font-black text-gray-900">
              <span>Total Paid Amount</span>
              <span className="text-2xl text-brand-600">{formatPrice(order.totalAmount)}</span>
            </div>
          </div>

        </div>

        {/* Right Column (5 cols): Delivery Destination, Payment Info, Help */}
        <div className="lg:col-span-5 space-y-6">

          {/* Delivery Information Card */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <div className="flex items-center space-x-3 pb-4 border-b border-gray-100">
              <div className="w-10 h-10 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center flex-shrink-0 font-bold">
                <MapPin className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-gray-900">Delivery Information</h3>
                <p className="text-xs text-gray-500">Destination address &amp; instructions</p>
              </div>
            </div>

            <div className="mt-5 space-y-4 text-xs">
              
              {/* Customer Name */}
              <div>
                <span className="font-bold uppercase tracking-wider text-gray-400 block mb-0.5">
                  Recipient
                </span>
                <p className="font-bold text-gray-900 flex items-center space-x-1.5">
                  <User className="w-3.5 h-3.5 text-gray-400" />
                  <span>{order.customerName || 'Customer'}</span>
                </p>
                {order.customerEmail && (
                  <p className="text-gray-500 text-[11px] mt-0.5">{order.customerEmail}</p>
                )}
              </div>

              {/* Delivery Address */}
              <div>
                <span className="font-bold uppercase tracking-wider text-gray-400 block mb-0.5">
                  Delivery Address
                </span>
                <p className="text-gray-800 font-semibold leading-relaxed">
                  {order.deliveryAddress || 'Default Delivery Address'}
                </p>
              </div>

              {/* Special Instructions */}
              {order.specialInstructions && (
                <div className="p-3 rounded-2xl bg-amber-50/70 border border-amber-200/70 text-amber-900">
                  <span className="font-bold flex items-center space-x-1 mb-0.5">
                    <FileText className="w-3.5 h-3.5 text-amber-600" />
                    <span>Special Instructions:</span>
                  </span>
                  <p className="italic text-amber-800">"{order.specialInstructions}"</p>
                </div>
              )}

              {/* Backend Delivery Note */}
              <div className="pt-2 border-t border-gray-100 flex items-start space-x-2 text-[11px] text-gray-500">
                <Truck className="w-4 h-4 text-gray-400 flex-shrink-0 mt-0.5" />
                <span>
                  Delivery partner assignment and live dispatch are coordinated by the restaurant kitchen and Foodify delivery fleet.
                </span>
              </div>

            </div>
          </div>

          {/* Payment Method & Status Card */}
          <div className="bg-white rounded-3xl p-6 border border-gray-200/80 shadow-sm">
            <div className="flex items-center space-x-3 pb-4 border-b border-gray-100">
              <div className="w-10 h-10 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center flex-shrink-0 font-bold">
                <CreditCard className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-gray-900">Payment Information</h3>
                <p className="text-xs text-gray-500">Method and settlement status</p>
              </div>
            </div>

            <div className="mt-5 space-y-3.5 text-xs">
              
              {/* Method */}
              <div className="flex items-center justify-between">
                <span className="text-gray-500">Payment Mode:</span>
                <span className="font-extrabold text-gray-900 flex items-center space-x-1.5">
                  {isCod ? (
                    <Banknote className="w-4 h-4 text-emerald-600" />
                  ) : paymentMethodUpper === 'UPI' ? (
                    <QrCode className="w-4 h-4 text-brand-600" />
                  ) : (
                    <CreditCard className="w-4 h-4 text-blue-600" />
                  )}
                  <span>{order.paymentMethod || 'COD'}</span>
                </span>
              </div>

              {/* Status */}
              <div className="flex items-center justify-between">
                <span className="text-gray-500">Payment Status:</span>
                <div>
                  {isPaid ? (
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-emerald-100 text-emerald-800">
                      <CheckCircle2 className="w-3 h-3 mr-1 text-emerald-600" />
                      Paid (SUCCESS)
                    </span>
                  ) : isRefunded ? (
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-purple-100 text-purple-800">
                      Refunded (REFUNDED)
                    </span>
                  ) : (
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-amber-100 text-amber-800">
                      Due on Delivery ({order.paymentStatus || 'PENDING'})
                    </span>
                  )}
                </div>
              </div>

              {/* Transaction ID if present */}
              {order.transactionId && (
                <div className="flex items-center justify-between pt-1">
                  <span className="text-gray-500">Transaction ID:</span>
                  <span className="font-mono font-bold text-gray-700 bg-gray-50 px-2 py-0.5 rounded-md border border-gray-200">
                    {order.transactionId}
                  </span>
                </div>
              )}

              {/* Total Paid */}
              <div className="pt-3 border-t border-gray-100 flex items-center justify-between font-bold">
                <span className="text-gray-600">Settled Amount:</span>
                <span className="text-sm font-black text-brand-600">
                  {formatPrice(order.totalAmount)}
                </span>
              </div>

            </div>
          </div>

          {/* Safety & Quality Assurance */}
          <div className="p-4 rounded-2xl bg-gray-50 border border-gray-100 text-xs text-gray-500 flex items-start space-x-3">
            <ShieldCheck className="w-5 h-5 text-emerald-600 flex-shrink-0 mt-0.5" />
            <p className="leading-relaxed">
              Every meal is prepared in hygienic kitchens following strict Foodify 2.0 safety standards. Have an issue with your meal? Contact support with your Order ID #{order.id}.
            </p>
          </div>

        </div>

      </div>

      {/* Cancel Order Confirmation Modal */}
      <CancelOrderModal
        isOpen={cancelModalOpen}
        order={order}
        onClose={() => setCancelModalOpen(false)}
        onSuccess={handleCancelSuccess}
      />

    </div>
  );
};

export default OrderDetailPage;

