import React, { useState } from 'react';
import { AlertTriangle, X, RefreshCw } from 'lucide-react';
import { formatPrice } from '../../utils/constants';

/**
 * CancelOrderModal Component
 * 
 * Confirmation dialog before calling PUT /myOrders/{orderId}/cancel
 * Enforces backend cancellation rules:
 * - Only PENDING orders can be cancelled.
 * - Explains refund mechanism for prepaid (UPI/Card) vs COD orders.
 */
const CancelOrderModal = ({ isOpen, order, onClose, onSuccess }) => {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen || !order) return null;

  const isPrepaid =
    (order.paymentStatus || '').toUpperCase() === 'SUCCESS' &&
    ['UPI', 'CARD'].includes((order.paymentMethod || '').toUpperCase());

  const handleConfirmCancel = async () => {
    try {
      setSubmitting(true);
      setError(null);
      await onSuccess(order.id);
      onClose();
    } catch (err) {
      console.error('Cancellation error:', err);
      const msg =
        err.response?.data?.message ||
        'Unable to cancel this order. Only PENDING orders can be cancelled.';
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/50 backdrop-blur-sm transition-opacity animate-in fade-in duration-200"
        onClick={submitting ? undefined : onClose}
      />

      {/* Modal Dialog */}
      <div className="flex min-h-full items-center justify-center p-4 text-center">
        <div className="relative w-full max-w-md transform overflow-hidden rounded-3xl bg-white p-6 sm:p-7 text-left shadow-2xl transition-all border border-gray-100 animate-in zoom-in-95 duration-200">
          
          {/* Close X Button */}
          {!submitting && (
            <button
              onClick={onClose}
              className="absolute top-5 right-5 p-1 rounded-full text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition"
              aria-label="Close"
            >
              <X className="w-5 h-5" />
            </button>
          )}

          {/* Warning Icon */}
          <div className="w-14 h-14 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center mb-5 mx-auto sm:mx-0 shadow-sm border border-rose-100">
            <AlertTriangle className="w-7 h-7" />
          </div>

          {/* Heading */}
          <h3 className="text-xl font-black text-gray-900 tracking-tight">
            Cancel Order #{order.id}?
          </h3>
          <p className="mt-2 text-xs sm:text-sm text-gray-600">
            Are you sure you want to cancel your order from{' '}
            <strong className="text-gray-900">{order.restaurantName || 'this restaurant'}</strong>?
          </p>

          {/* Policy & Refund Information */}
          <div className="mt-4 p-3.5 rounded-2xl bg-amber-50/80 border border-amber-200 text-xs text-amber-900 space-y-1.5">
            <p className="font-bold">Important Cancellation Details:</p>
            {isPrepaid ? (
              <p className="text-amber-800">
                • Your payment of <strong>{formatPrice(order.totalAmount)}</strong> will be automatically marked as <strong>REFUNDED</strong> in your receipt.
              </p>
            ) : (
              <p className="text-amber-800">
                • Since this order was placed with <strong>Cash on Delivery (COD)</strong>, no money was charged.
              </p>
            )}
            <p className="text-amber-800">
              • Cancellation is only permitted while your order is still in <strong>PENDING</strong> status.
            </p>
          </div>

          {/* Error Message */}
          {error && (
            <div className="mt-4 p-3 rounded-xl bg-rose-50 border border-rose-200 text-xs text-rose-700 font-semibold">
              {error}
            </div>
          )}

          {/* Action Buttons */}
          <div className="mt-6 flex flex-col sm:flex-row items-center gap-3">
            <button
              type="button"
              disabled={submitting}
              onClick={handleConfirmCancel}
              className="w-full sm:flex-1 py-3 px-4 rounded-xl font-bold text-sm text-white bg-rose-600 hover:bg-rose-700 active:scale-[0.99] transition shadow-md shadow-rose-600/20 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2"
            >
              {submitting ? (
                <>
                  <RefreshCw className="w-4 h-4 animate-spin" />
                  <span>Cancelling...</span>
                </>
              ) : (
                <span>Yes, Cancel Order</span>
              )}
            </button>
            <button
              type="button"
              disabled={submitting}
              onClick={onClose}
              className="w-full sm:flex-1 py-3 px-4 rounded-xl font-bold text-sm text-gray-700 bg-gray-100 hover:bg-gray-200 transition disabled:opacity-50"
            >
              Keep Order
            </button>
          </div>

        </div>
      </div>
    </div>
  );
};

export default CancelOrderModal;

