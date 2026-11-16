import React from 'react';
import { AlertCircle, RotateCcw } from 'lucide-react';

/**
 * Reusable Error Alert with optional Retry action
 */
const ErrorAlert = ({ message = 'Something went wrong.', onRetry }) => {
  return (
    <div className="max-w-xl mx-auto my-6 p-4 rounded-2xl bg-red-50 border border-red-200 text-red-800 text-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 shadow-sm">
      <div className="flex items-center space-x-2.5">
        <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-600" />
        <span className="font-medium">{message}</span>
      </div>
      {onRetry && (
        <button
          onClick={onRetry}
          className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-red-100 hover:bg-red-200 text-red-700 text-xs font-semibold transition"
        >
          <RotateCcw className="w-3.5 h-3.5" />
          <span>Retry</span>
        </button>
      )}
    </div>
  );
};

export default ErrorAlert;

