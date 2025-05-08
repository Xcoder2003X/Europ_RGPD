import React from "react";
import { ExclamationCircleIcon } from "@heroicons/react/24/solid";

export const ErrorMessage = ({ error }) => (
  <div className="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50">
    <div className="max-w-md w-full mx-4 bg-red-900/30 border border-red-500/50 rounded-xl p-6 shadow-2xl shadow-red-900/20">
      <div className="flex items-start gap-4">
        <ExclamationCircleIcon className="w-12 h-12 text-red-400 flex-shrink-0" />
        <div className="space-y-2">
          <h2 className="text-xl font-bold text-red-200">
            Erreur de chargement
          </h2>
          <p className="text-red-300 font-medium">
            {error?.message || "Une erreur inattendue est survenue"}
          </p>
          <p className="text-sm text-red-400/80 mt-2">
            Veuillez rafraîchir la page ou réessayer ultérieurement.
          </p>
        </div>
      </div>
    </div>
  </div>
);