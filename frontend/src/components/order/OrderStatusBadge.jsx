import React from 'react';
import {
  Clock,
  CheckCircle2,
  ChefHat,
  Truck,
  CheckCircle,
  XCircle
} from 'lucide-react';

/**
 * OrderStatusBadge Component
 * 
 * Maps actual Spring Boot Order status strings to formatted labels,
 * badges, and icons:
 * - PENDING          -> Amber (Order Placed)
 * - CONFIRMED        -> Blue (Confirmed by Restaurant)
 * - PREPARING        -> Orange (Kitchen Cooking)
 * - OUT_FOR_DELIVERY -> Purple (Out for Delivery)
 * - DELIVERED        -> Emerald (Delivered)
 * - CANCELLED        -> Rose/Red (Cancelled)
 */
export const getStatusMeta = (status) => {
  const norm = (status || '').toUpperCase();

  switch (norm) {
    case 'PENDING':
      return {
        label: 'Order Placed',
        badgeClass: 'bg-amber-100 text-amber-800 border-amber-200',
        dotClass: 'bg-amber-500 animate-pulse',
        Icon: Clock,
        stepIndex: 0,
        description: 'Waiting for restaurant confirmation',
      };
    case 'CONFIRMED':
      return {
        label: 'Confirmed',
        badgeClass: 'bg-blue-100 text-blue-800 border-blue-200',
        dotClass: 'bg-blue-500',
        Icon: CheckCircle2,
        stepIndex: 1,
        description: 'Restaurant has accepted your order',
      };
    case 'PREPARING':
      return {
        label: 'Preparing',
        badgeClass: 'bg-orange-100 text-orange-800 border-orange-200',
        dotClass: 'bg-orange-500 animate-pulse',
        Icon: ChefHat,
        stepIndex: 2,
        description: 'Chefs are cooking your fresh meal',
      };
    case 'OUT_FOR_DELIVERY':
      return {
        label: 'Out for Delivery',
        badgeClass: 'bg-purple-100 text-purple-800 border-purple-200',
        dotClass: 'bg-purple-500 animate-bounce',
        Icon: Truck,
        stepIndex: 3,
        description: 'Rider is on the way to your address',
      };
    case 'DELIVERED':
      return {
        label: 'Delivered',
        badgeClass: 'bg-emerald-100 text-emerald-800 border-emerald-200',
        dotClass: 'bg-emerald-600',
        Icon: CheckCircle,
        stepIndex: 4,
        description: 'Delivered successfully. Enjoy your food!',
      };
    case 'CANCELLED':
      return {
        label: 'Cancelled',
        badgeClass: 'bg-rose-100 text-rose-800 border-rose-200',
        dotClass: 'bg-rose-600',
        Icon: XCircle,
        stepIndex: -1,
        description: 'This order was cancelled',
      };
    default:
      return {
        label: norm || 'UNKNOWN',
        badgeClass: 'bg-gray-100 text-gray-800 border-gray-200',
        dotClass: 'bg-gray-500',
        Icon: Clock,
        stepIndex: 0,
        description: 'Order status update pending',
      };
  }
};

const OrderStatusBadge = ({ status, size = 'sm', showDot = true }) => {
  const meta = getStatusMeta(status);
  const Icon = meta.Icon;

  const sizeClasses =
    size === 'lg'
      ? 'px-3.5 py-1.5 text-xs font-extrabold'
      : size === 'md'
      ? 'px-3 py-1 text-xs font-bold'
      : 'px-2.5 py-0.5 text-[11px] font-bold';

  return (
    <span
      className={`inline-flex items-center rounded-full border ${meta.badgeClass} ${sizeClasses}`}
    >
      {showDot && (
        <span
          className={`w-1.5 h-1.5 rounded-full mr-1.5 flex-shrink-0 ${meta.dotClass}`}
        />
      )}
      <Icon className="w-3.5 h-3.5 mr-1 flex-shrink-0" />
      <span>{meta.label}</span>
    </span>
  );
};

export default OrderStatusBadge;

