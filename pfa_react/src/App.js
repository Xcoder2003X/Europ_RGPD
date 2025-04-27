import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link, useLocation } from 'react-router-dom';
import Signup from './components/Signup';
import Login from './components/Login';
import FileUpload from './components/fileUpload/FileUpload';
import FirstSteps from './components/firstSteps/FirstSteps';
import Dashboard from './components/Dashb/Dashboard';
import LanguageSwitcher from './components/LanguageSwitcher';
import './App.css';

function AppContent() {
  const location = useLocation(); // Use the hook here

  return (
    <div className="app">
      {/* Conditionally render navbar */}
      {!location.pathname.startsWith('/admin/dashboard') && (
        <nav className="navbar">
          <h1 className="logo"></h1>

          <div className="nav-links">
            <Link className="animated-button" to="/">
              <span>📝 registrer</span>
              <span></span>
            </Link>
            <Link className="animated-button" to="/login">
              <span>🔑 connect</span>
              <span></span>
            </Link>
            <div className="language-switcher-container absolute top-[5px] right-0">
              <LanguageSwitcher /> {/* Language Switcher Component */}
            </div>
          </div>
        </nav>
      )}
      <Routes>
        <Route path="/" element={<Signup />} />
        <Route path="/login" element={<Login />} />
        <Route path="/welcome" element={<FirstSteps />} />
        <Route path="/upload" element={<FileUpload />} />
        <Route path="/admin/dashboard" element={<Dashboard />} />
      </Routes>
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}