import React from 'react';
import { SearchX } from 'lucide-react';

/**
 * Reusable Empty State Component
 */
const EmptyState = ({
  icon: Icon = SearchX,
  title = 'No Results Found',
  description = "We couldn't find anything matching your request.",
  actionText,
  onAction,
}) => {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-4 text-center max-w-md mx-auto">
      <div className="w-16 h-16 rounded-2xl bg-orange-50 text-brand-500 flex items-center justify-center mb-4">
        <Icon className="w-8 h-8" />
      </div>
      <h3 className="text-lg font-bold text-gray-900">{title}</h3>
      <p className="mt-1 text-sm text-gray-500">{description}</p>
      {actionText && onAction && (
        <button
          onClick={onAction}
          className="mt-6 px-5 py-2.5 rounded-full bg-brand-500 hover:bg-brand-600 text-white text-sm font-semibold shadow-md shadow-brand-500/20 transition"
        >
          {actionText}
        </button>
      )}
    </div>
  );
};

export default EmptyState;

