import React from "react";
import { useTranslation } from "react-i18next";

const LanguageSwitcher = () => {
  const { i18n } = useTranslation();

  const changeLanguage = (lang) => {
    i18n.changeLanguage(lang);
  };

  return (
    
<div class="flex flex-col space-y-1 p-1 text-sm ">
  <label class="relative flex items-center cursor-pointer">
    <input
      checked=""
      class="sr-only peer"
      name="futuristic-radio"
      type="radio"
      onClick={() => changeLanguage("en")}
    />
    <div
      class="w-4 h-4 bg-transparent border-2 border-red-500 rounded-full peer-checked:bg-red-500 peer-checked:border-red-500 peer-hover:shadow-lg peer-hover:shadow-red-500/50 peer-checked:shadow-lg peer-checked:shadow-red-500/50 transition duration-300 ease-in-out"
    ></div>
    <span class="ml-2 text-white">English</span>
  </label>
  <label class="relative flex items-center cursor-pointer">
    <input class="sr-only peer" name="futuristic-radio" type="radio" onClick={() => changeLanguage("fr")} />
    <div
      class="w-4 h-4 bg-transparent border-2 border-yellow-500 rounded-full peer-checked:bg-yellow-500 peer-checked:border-yellow-500 peer-hover:shadow-lg peer-hover:shadow-yellow-500/50 peer-checked:shadow-lg peer-checked:shadow-yellow-500/50 transition duration-300 ease-in-out"
    ></div>
    <span class="ml-2 text-white">French</span>
  </label>
  <label class="relative flex items-center cursor-pointer">
    <input class="sr-only peer" name="futuristic-radio" type="radio" onClick={() => changeLanguage("ar")}/>
    <div
      class="w-4 h-4 bg-transparent border-2 border-green-500 rounded-full peer-checked:bg-green-500 peer-checked:border-green-500 peer-hover:shadow-lg peer-hover:shadow-green-500/50 peer-checked:shadow-lg peer-checked:shadow-green-500/50 transition duration-300 ease-in-out"
    ></div>
    <span class="ml-2 text-white">العربية</span>
  </label>
</div>

  );
};

export default LanguageSwitcher;