import React, { useState, useEffect } from 'react';
import { useParams, useLocation, Link, useNavigate } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import { formatPrice, getMenuItemImage } from '../utils/constants';
import LoadingSpinner from '../components/common/LoadingSpinner';
import {
  CheckCircle2,
  MapPin,
  Clock,
  Store,
  CreditCard,
  Banknote,
  QrCode,
  FileText,
  ArrowRight,
  Receipt
} from 'lucide-react';

/**
 * OrderSuccessPage Component
 * 
 * Order confirmation and receipt display for customer orders:
 * - Displays official Order ID, Date/Time, and Status.
 * - Displays payment method, simulated transaction ID, and payment status.
 * - Displays itemized breakdown and delivery address.
 * - If location.state is lost (e.g. page refreshed), fetches the order via GET /myOrders/{orderId}.
 */
const OrderSuccessPage = () => {
  const { orderId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  // Try to use order passed via navigate(..., { state: { order } })
  const [order, setOrder] = useState(location.state?.order || null);
  const [loading, setLoading] = useState(!location.state?.order);
  const [error, setError] = useState(null);

  // If order is not in navigation state, fetch from backend GET /myOrders/{orderId}
  useEffect(() => {
    if (!order && orderId) {
      const fetchOrderDetails = async () => {
        try {
          setLoading(true);
          setError(null);
          const data = await orderApi.getMyOrderById(orderId);
          setOrder(data);
        } catch (err) {
          console.error('Failed to fetch order details:', err);
          setError('Could not load order details. Please verify your order ID.');
        } finally {
          setLoading(false);
        }
      };

      fetchOrderDetails();
    }
  }, [order, orderId]);

  if (loading) {
    return (
      <div className="py-24 flex justify-center">
        <LoadingSpinner text="Loading your order receipt..." />
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="max-w-xl mx-auto px-4 py-16 text-center">
        <div className="w-16 h-16 bg-red-50 text-red-500 rounded-3xl mx-auto flex items-center justify-center mb-4">
          <FileText className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-bold text-gray-900">Order Not Found</h2>
        <p className="mt-2 text-sm text-gray-600">
          {error || `We couldn't retrieve details for order #${orderId}.`}
        </p>
        <div className="mt-6 flex justify-center space-x-3">
          <Link
            to="/"
            className="px-6 py-2.5 rounded-xl bg-brand-500 text-white font-bold text-sm hover:bg-brand-600 transition"
          >
            Go to Home
          </Link>
        </div>
      </div>
    );
  }

  // Format date helper
  const formattedDate = order.orderDate
    ? new Date(order.orderDate).toLocaleString('en-IN', {
        dateStyle: 'medium',
        timeStyle: 'short',
      })
    : 'Just now';

  const isCod = (order.paymentMethod || '').toUpperCase() === 'COD';
  const isPaid = (order.paymentStatus || '').toUpperCase() === 'SUCCESS';

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-10">
      
      {/* Success Celebration Header */}
      <div className="text-center mb-8">
        <div className="w-20 h-20 bg-emerald-50 text-emerald-600 rounded-full mx-auto flex items-center justify-center mb-4 shadow-sm border border-emerald-100 animate-bounce">
          <CheckCircle2 className="w-10 h-10" />
        </div>
        <span className="inline-block px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider text-emerald-700 bg-emerald-100 mb-2">
          Order Confirmed
        </span>
        <h1 className="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
          Thank you for your order!
        </h1>
        <p className="mt-1 text-sm text-gray-500">
          Your order has been received and forwarded to the restaurant kitchen.
        </p>
      </div>

      {/* Main Order Card */}
      <div className="bg-white rounded-3xl border border-gray-200/80 shadow-sm overflow-hidden mb-8">
        
        {/* Order Meta Bar */}
        <div className="p-6 bg-gradient-to-r from-orange-50/60 via-amber-50/40 to-white border-b border-gray-100 flex flex-wrap items-center justify-between gap-4">
          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500 block">
              Order Number
            </span>
            <span className="text-xl font-black text-gray-900">
              #{order.id}
            </span>
          </div>

          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500 block">
              Order Placed At
            </span>
            <span className="text-sm font-bold text-gray-800 flex items-center space-x-1 mt-0.5">
              <Clock className="w-3.5 h-3.5 text-gray-400" />
              <span>{formattedDate}</span>
            </span>
          </div>

          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500 block">
              Order Status
            </span>
            <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-extrabold bg-amber-100 text-amber-800 mt-0.5">
              <span className="w-1.5 h-1.5 rounded-full bg-amber-500 mr-1.5 animate-pulse" />
              {order.status || 'PENDING'}
            </span>
          </div>

          <div>
            <span className="text-xs font-bold uppercase tracking-wider text-gray-500 block">
              Total Amount
            </span>
            <span className="text-xl font-black text-brand-600">
              {formatPrice(order.totalAmount)}
            </span>
          </div>
        </div>

        {/* Details Grid */}
        <div className="p-6 sm:p-8 space-y-8">
          
          {/* Restaurant & Delivery Row */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pb-6 border-b border-gray-100">
            
            {/* Restaurant Details */}
            <div className="flex items-start space-x-3.5">
              <div className="w-10 h-10 rounded-2xl bg-orange-100/70 text-brand-600 flex items-center justify-center flex-shrink-0">
                <Store className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-gray-500 block">
                  Restaurant
                </span>
                <h4 className="text-base font-extrabold text-gray-900 mt-0.5">
                  {order.restaurantName || 'Restaurant'}
                </h4>
                <p className="text-xs text-gray-500 mt-0.5">
                  Preparing your fresh meal
                </p>
              </div>
            </div>

            {/* Delivery Address */}
            <div className="flex items-start space-x-3.5">
              <div className="w-10 h-10 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center flex-shrink-0">
                <MapPin className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-gray-500 block">
                  Delivery Destination
                </span>
                <p className="text-sm font-semibold text-gray-800 mt-0.5">
                  {order.deliveryAddress || 'Default Delivery Address'}
                </p>
                {order.specialInstructions && (
                  <p className="text-xs text-gray-500 mt-1 italic">
                    Note: "{order.specialInstructions}"
                  </p>
                )}
              </div>
            </div>

          </div>

          {/* Payment Status Summary */}
          <div className="p-5 rounded-2xl bg-gray-50 border border-gray-100 flex flex-wrap items-center justify-between gap-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-xl bg-white border border-gray-200 text-gray-700 flex items-center justify-center flex-shrink-0">
                {isCod ? (
                  <Banknote className="w-5 h-5 text-emerald-600" />
                ) : (order.paymentMethod || '').toUpperCase() === 'UPI' ? (
                  <QrCode className="w-5 h-5 text-brand-600" />
                ) : (
                  <CreditCard className="w-5 h-5 text-blue-600" />
                )}
              </div>
              <div>
                <div className="flex items-center space-x-2">
                  <span className="text-xs font-bold uppercase tracking-wider text-gray-500">
                    Payment Method:
                  </span>
                  <span className="text-sm font-extrabold text-gray-900">
                    {order.paymentMethod || 'COD'}
                  </span>
                </div>
                {order.transactionId && (
                  <p className="text-xs text-gray-500 mt-0.5 font-mono">
                    Txn ID: {order.transactionId}
                  </p>
                )}
              </div>
            </div>

            <div>
              {isPaid ? (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                  <CheckCircle2 className="w-3.5 h-3.5 mr-1 text-emerald-600" />
                  Paid Successfully
                </span>
              ) : (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-amber-100 text-amber-800">
                  <Clock className="w-3.5 h-3.5 mr-1 text-amber-600" />
                  Payment Due on Delivery ({order.paymentStatus || 'PENDING'})
                </span>
              )}
            </div>
          </div>

          {/* Itemized Order Table */}
          <div>
            <h4 className="text-sm font-bold uppercase tracking-wider text-gray-700 mb-3 flex items-center space-x-1.5">
              <Receipt className="w-4 h-4 text-brand-500" />
              <span>Ordered Items ({order.items?.length || 0})</span>
            </h4>

            <div className="divide-y divide-gray-100 border border-gray-100 rounded-2xl overflow-hidden">
              {order.items && order.items.length > 0 ? (
                order.items.map((item, idx) => (
                  <div key={item.id || idx} className="p-4 flex items-center justify-between text-sm hover:bg-gray-50/50 transition">
                    <div className="flex items-center space-x-3 min-w-0">
                      <img
                        src={getMenuItemImage(item.menuItemId, item.menuItemName)}
                        alt={item.menuItemName}
                        className="w-12 h-12 rounded-xl object-cover flex-shrink-0 border border-gray-100"
                      />
                      <div className="truncate">
                        <p className="font-bold text-gray-900 truncate">{item.menuItemName}</p>
                        <p className="text-xs text-gray-500">
                          {formatPrice(item.price)} × {item.quantity}
                        </p>
                      </div>
                    </div>
                    <span className="font-extrabold text-gray-900">
                      {formatPrice(item.subTotal)}
                    </span>
                  </div>
                ))
              ) : (
                <p className="p-4 text-xs text-gray-500">No items available in receipt.</p>
              )}
            </div>

            {/* Total Footer */}
            <div className="pt-4 flex justify-between items-center text-base font-extrabold text-gray-900">
              <span>Grand Total</span>
              <span className="text-xl text-brand-600">{formatPrice(order.totalAmount)}</span>
            </div>
          </div>

        </div>

      </div>

      {/* Tracking & Navigation Actions */}
      <div className="flex flex-col sm:flex-row justify-center items-center gap-3">
        <Link
          to={`/orders/${order.id}`}
          className="w-full sm:w-auto px-8 py-3.5 rounded-2xl bg-brand-500 text-white font-extrabold text-sm hover:bg-brand-600 transition shadow-md shadow-brand-500/20 text-center flex items-center justify-center space-x-2"
        >
          <span>Track Order &amp; Details</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
        <Link
          to="/orders"
          className="w-full sm:w-auto px-6 py-3.5 rounded-2xl bg-white border border-gray-200 text-gray-700 font-bold text-sm hover:bg-gray-50 transition text-center"
        >
          View All My Orders
        </Link>
        <Link
          to="/"
          className="w-full sm:w-auto px-6 py-3.5 rounded-2xl bg-gray-100 text-gray-700 font-bold text-sm hover:bg-gray-200 transition text-center"
        >
          Explore More Food
        </Link>
      </div>

    </div>
  );
};

export default OrderSuccessPage;

