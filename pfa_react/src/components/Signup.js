import React, { useState } from "react";
import axios from "axios";
import "./signup.css";
import AuthService from "../services/AuthService";
const SignUp = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("ROLE_USER"); // Default to "ROLE_USER"
  const currentUser = AuthService.getCurrentUser();
  const isAdmin = currentUser?.role === "ROLE_ADMIN";
  
  const handleSubmit = async (e) => {
    e.preventDefault();

    const signUpData = {
      username,
      password,
      role,
    };

    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/signup",
        signUpData
      );
      alert(response.data);
    } catch (error) {
      console.error("Error during signup:", error);
      alert("There was an error during signup!");
    }
  };

  return (
    <form className="form" onSubmit={handleSubmit}>
      <p className="title">Register </p>
      <p className="message">Signup now and get full access to our app. </p>

      <label>
        <input
          className="input"
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <span>Username</span>
      </label>

      <label>
        <input
          className="input"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <span>Password</span>
      </label>
      <div style={{ display: "flex", gap: "20px", margin: "10px" }}>
        <label>Role:</label>
        <select
          value={role}
          onChange={(e) => setRole(e.target.value)}
          disabled={!isAdmin} // Disable the admin role selection for non-admin users
        >
          <option value="ROLE_USER">User</option>
          {isAdmin && <option value="ROLE_ADMIN">Admin</option>}
        </select>
      </div>
      <button className="submit">Submit</button>
      <p className="signin">
        Already have an acount ? <a href="login">Login</a>{" "}
      </p>
    </form>
  );
};

export default SignUp;
