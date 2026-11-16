import React from 'react';

/**
 * Reusable Loading Spinner with Foodify styling
 */
const LoadingSpinner = ({ message, text = 'Loading...' }) => {
  const displayText = message || text;
  return (
    <div className="flex flex-col items-center justify-center py-16 px-4">
      <div className="relative w-14 h-14">
        <div className="w-14 h-14 rounded-full border-4 border-orange-100 border-t-brand-500 animate-spin"></div>
      </div>
      <p className="mt-4 text-sm font-medium text-gray-500 animate-pulse">{displayText}</p>
    </div>
  );
};

export default LoadingSpinner;

