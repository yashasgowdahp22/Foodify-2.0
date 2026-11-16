import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import { formatPrice, getMenuItemImage } from '../utils/constants';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorAlert from '../components/common/ErrorAlert';
import OrderStatusBadge from '../components/order/OrderStatusBadge';
import CancelOrderModal from '../components/order/CancelOrderModal';
import {
  ShoppingBag,
  Store,
  Calendar,
  CreditCard,
  Banknote,
  QrCode,
  Eye,
  RefreshCw,
  CheckCircle2
} from 'lucide-react';

/**
 * OrdersPage Component
 * 
 * Customer Order History page (/orders):
 * - Fetches customer orders from backend GET /myOrders
 * - Displays order ID, date, restaurant, food items, total amount, payment method, and status.
 * - Supports filtering by All, Active, Completed, and Cancelled orders.
 * - Allows cancelling PENDING orders via confirmation modal and PUT /myOrders/{orderId}/cancel.
 */
const OrdersPage = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successBanner, setSuccessBanner] = useState(null);

  // Active filter tab: 'ALL' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED'
  const [activeTab, setActiveTab] = useState('ALL');

  // Cancel order modal state
  const [cancelModalOrder, setCancelModalOrder] = useState(null);

  // Fetch orders from backend
  const loadOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await orderApi.getMyOrders();
      setOrders(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Failed to load customer orders:', err);
      const msg = err.response?.data?.message || 'Unable to load your orders. Please try again.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  // Handle cancellation execution
  const handleCancelOrder = async (orderId) => {
    const updatedOrder = await orderApi.cancelMyOrder(orderId);
    
    // Update local state with the newly cancelled order
    setOrders((prev) =>
      prev.map((ord) => (ord.id === orderId ? updatedOrder : ord))
    );

    setSuccessBanner(`Order #${orderId} has been successfully cancelled.`);
    setTimeout(() => setSuccessBanner(null), 6000);
  };

  // Filter orders according to active tab
  const filteredOrders = orders.filter((order) => {
    const status = (order.status || '').toUpperCase();
    if (activeTab === 'ACTIVE') {
      return ['PENDING', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY'].includes(status);
    }
    if (activeTab === 'COMPLETED') {
      return status === 'DELIVERED';
    }
    if (activeTab === 'CANCELLED') {
      return status === 'CANCELLED';
    }
    return true; // 'ALL'
  });

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-10">
      
      {/* Header & Page Title */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <div className="flex items-center space-x-2 text-xs font-semibold text-gray-500 mb-1">
            <Link to="/" className="hover:text-brand-600">Home</Link>
            <span>/</span>
            <span className="text-brand-600 font-bold">My Orders</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
            My Orders &amp; Receipts
          </h1>
          <p className="mt-1 text-sm text-gray-500">
            Track current deliveries, review past orders, and download receipts.
          </p>
        </div>

        {/* Refresh Button */}
        <button
          onClick={loadOrders}
          disabled={loading}
          className="inline-flex items-center space-x-2 px-4 py-2.5 rounded-xl border border-gray-200 bg-white text-xs font-bold text-gray-700 hover:bg-gray-50 hover:text-brand-600 transition shadow-sm self-start sm:self-auto disabled:opacity-50"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh Orders</span>
        </button>
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

      {/* Error Alert */}
      {error && (
        <div className="mb-6">
          <ErrorAlert message={error} onClose={() => setError(null)} />
        </div>
      )}

      {/* Filter Tabs */}
      <div className="flex items-center space-x-2 border-b border-gray-200 pb-3 mb-6 overflow-x-auto text-xs font-bold">
        {[
          { key: 'ALL', label: 'All Orders', count: orders.length },
          {
            key: 'ACTIVE',
            label: 'Active Orders',
            count: orders.filter((o) =>
              ['PENDING', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY'].includes((o.status || '').toUpperCase())
            ).length,
          },
          {
            key: 'COMPLETED',
            label: 'Completed',
            count: orders.filter((o) => (o.status || '').toUpperCase() === 'DELIVERED').length,
          },
          {
            key: 'CANCELLED',
            label: 'Cancelled',
            count: orders.filter((o) => (o.status || '').toUpperCase() === 'CANCELLED').length,
          },
        ].map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-3.5 py-2 rounded-xl transition whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === tab.key
                ? 'bg-brand-500 text-white shadow-sm shadow-brand-500/20'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            <span>{tab.label}</span>
            <span
              className={`text-[10px] px-1.5 py-0.2 rounded-full ${
                activeTab === tab.key ? 'bg-brand-700 text-white' : 'bg-gray-200 text-gray-700'
              }`}
            >
              {tab.count}
            </span>
          </button>
        ))}
      </div>

      {/* Loading State */}
      {loading && orders.length === 0 && (
        <div className="py-20 flex justify-center">
          <LoadingSpinner text="Loading your order history..." />
        </div>
      )}

      {/* Empty State */}
      {!loading && filteredOrders.length === 0 && (
        <div className="bg-white rounded-3xl p-10 sm:p-14 text-center border border-gray-200/80 shadow-sm max-w-xl mx-auto my-6">
          <div className="w-16 h-16 rounded-3xl bg-orange-50 text-brand-500 flex items-center justify-center mx-auto mb-4 border border-orange-100">
            <ShoppingBag className="w-8 h-8" />
          </div>
          <h3 className="text-xl font-black text-gray-900 tracking-tight">
            {activeTab === 'ALL'
              ? 'No Orders Placed Yet'
              : `No ${activeTab.toLowerCase()} orders found`}
          </h3>
          <p className="mt-2 text-xs sm:text-sm text-gray-500 max-w-sm mx-auto">
            {activeTab === 'ALL'
              ? 'Discover top-rated restaurants around you and place your first delicious meal!'
              : 'Try switching to another filter tab to see your other orders.'}
          </p>
          <div className="mt-6 flex justify-center space-x-3">
            <Link
              to="/"
              className="px-6 py-2.5 rounded-xl bg-brand-500 text-white font-bold text-xs hover:bg-brand-600 transition shadow-md shadow-brand-500/20"
            >
              Explore Restaurants
            </Link>
            {activeTab !== 'ALL' && (
              <button
                onClick={() => setActiveTab('ALL')}
                className="px-6 py-2.5 rounded-xl bg-gray-100 text-gray-700 font-bold text-xs hover:bg-gray-200 transition"
              >
                View All Orders
              </button>
            )}
          </div>
        </div>
      )}

      {/* Orders List Grid */}
      {!loading && filteredOrders.length > 0 && (
        <div className="space-y-5">
          {filteredOrders.map((order) => {
            const isPending = (order.status || '').toUpperCase() === 'PENDING';
            const formattedDate = order.orderDate
              ? new Date(order.orderDate).toLocaleString('en-IN', {
                  dateStyle: 'medium',
                  timeStyle: 'short',
                })
              : 'Date unavailable';

            const paymentMethodUpper = (order.paymentMethod || '').toUpperCase();
            const isCod = paymentMethodUpper === 'COD';

            return (
              <div
                key={order.id}
                className="bg-white rounded-3xl border border-gray-200/80 shadow-sm hover:shadow-md transition-shadow overflow-hidden"
              >
                {/* Order Card Top Bar */}
                <div className="p-5 sm:px-6 bg-gradient-to-r from-gray-50/80 to-white border-b border-gray-100 flex flex-wrap items-center justify-between gap-3">
                  <div className="flex items-center space-x-3">
                    <span className="text-sm font-black text-gray-900">
                      Order #{order.id}
                    </span>
                    <span className="text-gray-300">•</span>
                    <div className="flex items-center text-xs text-gray-500 space-x-1">
                      <Calendar className="w-3.5 h-3.5" />
                      <span>{formattedDate}</span>
                    </div>
                  </div>

                  {/* Order Status Badge */}
                  <div>
                    <OrderStatusBadge status={order.status} size="md" />
                  </div>
                </div>

                {/* Order Card Body */}
                <div className="p-5 sm:p-6 grid grid-cols-1 md:grid-cols-12 gap-5 items-center">
                  
                  {/* Restaurant & Item Preview (7 cols) */}
                  <div className="md:col-span-7 space-y-3">
                    <div className="flex items-center space-x-2">
                      <Store className="w-4 h-4 text-brand-600 flex-shrink-0" />
                      <h4 className="text-base font-extrabold text-gray-900 truncate">
                        {order.restaurantName || 'Restaurant'}
                      </h4>
                    </div>

                    {/* Dish Preview Icons */}
                    <div className="flex items-center space-x-2 overflow-x-auto py-1">
                      {order.items && order.items.length > 0 ? (
                        order.items.slice(0, 4).map((item, idx) => (
                          <div
                            key={item.id || idx}
                            className="flex items-center space-x-2 bg-gray-50 border border-gray-100 rounded-xl p-1.5 pr-2.5 flex-shrink-0"
                            title={`${item.menuItemName} (×${item.quantity})`}
                          >
                            <img
                              src={getMenuItemImage(item.menuItemId, item.menuItemName)}
                              alt={item.menuItemName}
                              className="w-8 h-8 rounded-lg object-cover"
                            />
                            <div className="text-[11px] leading-tight max-w-[120px] truncate">
                              <p className="font-bold text-gray-800 truncate">{item.menuItemName}</p>
                              <p className="text-gray-500">Qty: {item.quantity}</p>
                            </div>
                          </div>
                        ))
                      ) : (
                        <p className="text-xs text-gray-400">Items summary not available</p>
                      )}

                      {order.items && order.items.length > 4 && (
                        <span className="text-[11px] font-bold text-gray-500 bg-gray-100 px-2 py-1.5 rounded-lg flex-shrink-0">
                          +{order.items.length - 4} more
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Payment & Price (3 cols) */}
                  <div className="md:col-span-3 border-t md:border-t-0 md:border-l border-gray-100 pt-3 md:pt-0 md:pl-5 space-y-1">
                    <span className="text-[11px] font-bold uppercase tracking-wider text-gray-500 block">
                      Total Amount
                    </span>
                    <span className="text-xl font-black text-brand-600 block">
                      {formatPrice(order.totalAmount)}
                    </span>
                    <div className="flex items-center space-x-1.5 text-xs text-gray-600 pt-0.5">
                      {isCod ? (
                        <Banknote className="w-3.5 h-3.5 text-emerald-600" />
                      ) : paymentMethodUpper === 'UPI' ? (
                        <QrCode className="w-3.5 h-3.5 text-brand-600" />
                      ) : (
                        <CreditCard className="w-3.5 h-3.5 text-blue-600" />
                      )}
                      <span className="font-semibold">{order.paymentMethod || 'COD'}</span>
                      <span className="text-gray-400 text-[10px]">
                        ({order.paymentStatus || 'PENDING'})
                      </span>
                    </div>
                  </div>

                  {/* Actions (2 cols) */}
                  <div className="md:col-span-2 flex flex-col sm:flex-row md:flex-col gap-2 justify-end">
                    <Link
                      to={`/orders/${order.id}`}
                      className="px-4 py-2.5 rounded-xl bg-gray-100 hover:bg-brand-50 hover:text-brand-600 text-gray-800 font-bold text-xs transition text-center inline-flex items-center justify-center space-x-1.5"
                    >
                      <Eye className="w-3.5 h-3.5" />
                      <span>View Details</span>
                    </Link>

                    {/* Cancel button: Only allowed for PENDING orders */}
                    {isPending && (
                      <button
                        type="button"
                        onClick={() => setCancelModalOrder(order)}
                        className="px-4 py-2 rounded-xl text-rose-600 hover:text-rose-700 hover:bg-rose-50 border border-rose-200 font-bold text-xs transition text-center"
                      >
                        Cancel Order
                      </button>
                    )}
                  </div>

                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Cancel Order Confirmation Modal */}
      <CancelOrderModal
        isOpen={!!cancelModalOrder}
        order={cancelModalOrder}
        onClose={() => setCancelModalOrder(null)}
        onSuccess={handleCancelOrder}
      />

    </div>
  );
};

export default OrdersPage;

