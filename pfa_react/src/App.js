import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Link,
  useLocation,
} from "react-router-dom";
import Signup from "./components/Signup";
import Login from "./components/Login";
import FileUpload from "./components/fileUpload/FileUpload";
import FirstSteps from "./components/firstSteps/FirstSteps";
import Dashboard from "./components/Dashb/Dashboard";
import LanguageSwitcher from "./components/LanguageSwitcher";
import "./App.css";
import { useEffect } from "react";
import { MotionConfig, motion } from "framer-motion";
import "./i18n";
import { useTranslation } from "react-i18next";
import { QueryClient, QueryClientProvider } from "react-query";

// request avancees avec react-query
const queryClient = new QueryClient();
function AppContent() {
  const { t } = useTranslation();

  const location = useLocation(); // Use the hook here
  // Add cursor effect for AI vibe
  useEffect(() => {
    const cursor = document.createElement("div");
    cursor.className = "ai-cursor";
    document.body.appendChild(cursor);

    const moveCursor = (e) => {
      cursor.style.left = `${e.clientX}px`;
      cursor.style.top = `${e.clientY}px`;
    };

    document.addEventListener("mousemove", moveCursor);
    return () => document.removeEventListener("mousemove", moveCursor);
  }, []);

  return (
    <div className="app">
      {/* Conditionally render navbar */}

      {/* Add circuit board background pattern */}
      <div className="circuit-pattern"></div>

      {/* Animated gradient spheres */}
      <div className="gradient-sphere sphere-1"></div>
      <div className="gradient-sphere sphere-2"></div>

      {!location.pathname.startsWith("/admin/dashboard") && (
        <nav className="navbar">
          <div className="logo-container">
            <h1 className="logo"></h1>
            <span className="ai-pulse"></span>
          </div>

          <div className="nav-links">
            <Link className="holographic-button" to="/">
              <span className="icon">🚀</span>
              <span className="label">{t("nav_btn1")}</span>
              <div className="shine"></div>
            </Link>
            <Link className="holographic-button" to="/login">
              <span className="icon">🔐</span>
              <span className="label">{t("nav_btn2")}</span>
              <div className="shine"></div>
            </Link>
            <div className="language-switcher-container">
              <LanguageSwitcher />
            </div>
          </div>
        </nav>
      )}
      <Routes>
        <Route
          path="/"
          element={
            <AnimatedWrapper>
              <Signup />
            </AnimatedWrapper>
          }
        />
        <Route
          path="/login"
          element={
            <AnimatedWrapper>
              <Login />
            </AnimatedWrapper>
          }
        />
        <Route
          path="/welcome"
          element={
            <AnimatedWrapper>
              <FirstSteps />
            </AnimatedWrapper>
          }
        />
        <Route
          path="/upload"
          element={
            <AnimatedWrapper>
              <FileUpload />
            </AnimatedWrapper>
          }
        />
        <Route
          path="/admin/dashboard"
          element={
            <AnimatedWrapper>
              <Dashboard />
            </AnimatedWrapper>
          }
        />
      </Routes>
    </div>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <AppContent />
      </Router>
    </QueryClientProvider>
  );
}

// Update AnimatedWrapper component
const AnimatedWrapper = ({ children }) => (
  <MotionConfig
    transition={{
      type: "spring",
      mass: 0.5,
      stiffness: 100,
      damping: 15,
    }}
  >
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
    >
      {children}
    </motion.div>
  </MotionConfig>
);
