import React, { useState } from "react";
import AuthService from "../services/AuthService";
import "./signup.css";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [redirect, setRedirect] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    AuthService.login(username, password)
      .then((data) => {
        console.log("🔐 login response:", data);
        setMessage("Connexion réussie !");
        setRedirect(true);
        navigate(data.role === "ROLE_ADMIN" ? "/admin/dashboard" : "/welcome"); // Redirect to welcome for regular users
      })
      .catch((error) => {
        setMessage("Échec de la connexion, vérifiez vos identifiants.");
      });
  };

  if (redirect) {
    return null; // Remove the Hello Admin / User message since we're using navigate
  }

  return (
    <div>
      <form className="form" onSubmit={handleSubmit}>
        <p className="title">Login </p>
        <p className="message">Login now and get full access to our app. </p>

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

        <button className="submit">Submit</button>
        <p className="signin">
          <a href="/">Register</a>{" "}
        </p>
      </form>
      <p>{message}</p>
    </div>
  );
};

export default Login;
