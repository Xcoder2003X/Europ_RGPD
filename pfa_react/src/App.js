import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import Signup from './components/Signup';
import Login from './components/Login';
import FileUpload from './components/fileUpload/FileUpload';
import FirstSteps from './components/firstSteps/FirstSteps';
import Dashboard from './components/Dashb/Dashboard';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        {/* Custom Styled Navbar */}
        <nav className="navbar">
          <h1>🤖 AI Conform🤖</h1>
          <div className="nav-links">
            <Link className="button-analyse" to="/">Signup</Link>
            <Link className="button-analyse" to="/login">Login</Link>
            <Link className="button-analyse" to="/upload">File Upload</Link>
          </div>
        </nav>
        <Routes>
          <Route path="/" element={<Signup />} />
          <Route path="/login" element={<Login />} />
          <Route path="/welcome" element={<FirstSteps />} />
          <Route path="/upload" element={<FileUpload />} />
          <Route path="/admin/dashboard" element={<Dashboard />} />
        </Routes>
      </div>

      {/* Page Routes */}
    </Router>
  );
}

export default App;


