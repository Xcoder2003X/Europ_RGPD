import React from "react";

export const LoadingSpinner = ({ message = "Chargement en cours..." }) => (
  <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex flex-col items-center justify-center z-50">
    <div className="relative w-20 h-20 animate-spin">
      <svg className="absolute inset-0" viewBox="0 0 100 100">
        <circle
          className="text-blue-500"
          stroke="currentColor"
          strokeWidth="8"
          strokeLinecap="round"
          fill="none"
          strokeDasharray="180 150"
          cx="50"
          cy="50"
          r="40"
        />
      </svg>
      <div className="absolute inset-0 flex items-center justify-center">
        <div className="w-12 h-12 bg-blue-500 rounded-full animate-pulse" />
      </div>
    </div>
    <p className="mt-4 text-lg font-semibold text-gray-300 animate-pulse">
      {message}
    </p>
  </div>
);