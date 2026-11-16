import React from 'react';
import {
  Clock,
  CheckCircle2,
  ChefHat,
  Truck,
  CheckCircle,
  XCircle
} from 'lucide-react';

const ORDER_STEPS = [
  { key: 'PENDING', label: 'Order Placed', sublabel: 'Sent to restaurant', Icon: Clock },
  { key: 'CONFIRMED', label: 'Confirmed', sublabel: 'Accepted by kitchen', Icon: CheckCircle2 },
  { key: 'PREPARING', label: 'Preparing', sublabel: 'Food is being cooked', Icon: ChefHat },
  { key: 'OUT_FOR_DELIVERY', label: 'Out for Delivery', sublabel: 'Rider is on the way', Icon: Truck },
  { key: 'DELIVERED', label: 'Delivered', sublabel: 'Order completed', Icon: CheckCircle },
];

const STATUS_TO_INDEX = {
  PENDING: 0,
  CONFIRMED: 1,
  PREPARING: 2,
  OUT_FOR_DELIVERY: 3,
  DELIVERED: 4,
};

/**
 * OrderStatusTracker Component
 * 
 * Renders a visual status timeline stepper corresponding to the
 * backend state transitions.
 */
const OrderStatusTracker = ({ status }) => {
  const norm = (status || '').toUpperCase();
  const isCancelled = norm === 'CANCELLED';
  const currentStep = STATUS_TO_INDEX[norm] !== undefined ? STATUS_TO_INDEX[norm] : 0;

  if (isCancelled) {
    return (
      <div className="bg-rose-50/80 border border-rose-200 rounded-2xl p-5 text-rose-900 flex items-start space-x-3.5">
        <div className="w-10 h-10 rounded-xl bg-rose-100 text-rose-600 flex items-center justify-center flex-shrink-0">
          <XCircle className="w-6 h-6" />
        </div>
        <div>
          <h4 className="text-sm font-bold text-rose-900">This Order Has Been Cancelled</h4>
          <p className="text-xs text-rose-700 mt-0.5">
            The order was cancelled while in PENDING status. If you paid online via UPI or Credit Card, your payment status has been marked as <strong>REFUNDED</strong>.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl p-5 sm:p-6 border border-gray-100 shadow-sm">
      <h3 className="text-xs font-bold uppercase tracking-wider text-gray-500 mb-6">
        Order Status Tracker
      </h3>

      {/* Stepper container */}
      <div className="relative">
        
        {/* Horizontal Line on tablet/desktop */}
        <div className="hidden sm:block absolute top-5 left-8 right-8 h-1 bg-gray-100 -z-0">
          <div
            className="h-full bg-brand-500 transition-all duration-500"
            style={{
              width: `${(currentStep / (ORDER_STEPS.length - 1)) * 100}%`,
            }}
          />
        </div>

        {/* Steps */}
        <div className="grid grid-cols-1 sm:grid-cols-5 gap-4 sm:gap-2 relative z-10">
          {ORDER_STEPS.map((step, index) => {
            const isCompleted = index < currentStep;
            const isCurrent = index === currentStep;
            const isUpcoming = index > currentStep;
            const Icon = step.Icon;

            return (
              <div
                key={step.key}
                className="flex sm:flex-col items-center sm:text-center space-x-3 sm:space-x-0"
              >
                {/* Circle Icon Indicator */}
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 transition-colors duration-200 ${
                    isCompleted
                      ? 'bg-brand-500 text-white shadow-sm shadow-brand-500/30'
                      : isCurrent
                      ? 'bg-brand-600 text-white ring-4 ring-orange-100 shadow-md shadow-brand-500/20'
                      : 'bg-gray-100 text-gray-400'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                </div>

                {/* Text Labels */}
                <div className="sm:mt-2.5">
                  <p
                    className={`text-xs font-bold ${
                      isCurrent
                        ? 'text-brand-600'
                        : isCompleted
                        ? 'text-gray-900'
                        : 'text-gray-400'
                    }`}
                  >
                    {step.label}
                  </p>
                  <p className="text-[11px] text-gray-500 hidden sm:block mt-0.5">
                    {step.sublabel}
                  </p>
                </div>
              </div>
            );
          })}
        </div>

      </div>
    </div>
  );
};

export default OrderStatusTracker;

