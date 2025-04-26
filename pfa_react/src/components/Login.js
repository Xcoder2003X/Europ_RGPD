import React, { useState, useEffect } from "react";
import AuthService from "../services/AuthService";
import "./signup.css";
import { useNavigate } from "react-router-dom";
import Robot3D from "./Robot3D";
import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';
import { useTranslation } from "react-i18next";
import "../i18n"; // Ensure i18n is initialized

const fadeIn = keyframes`
  from { opacity: 0; }
  to { opacity: 1; }
`;

  const AnimatedContainer = styled.div`
    position: absolute;
    top: 150px;
    display: flex;
    align-items: center;
    gap: 40px;
    box-shadow: 4px 4px 10px 6px rgb(66, 66, 66);
    border-radius: 30px;
    padding: 30px;
    animation: ${fadeIn} 2s ease-out;
  `;
const Login = () => {
  const { t } = useTranslation();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [redirect, setRedirect] = useState(false);
  const [isExcited, setIsExcited] = useState(false); // State to control robot excitement
  const [robotImage, setRobotImage] = useState("/robot1.webP"); // Initial robot image
  const robotImages = ["/robot1.webP", "/robot2.webP", "/robot3.webP"]; // Array of robot images
  const [hasTyped, setHasTyped] = useState(false); // State to track if user has typed
  const navigate = useNavigate();

  


 

  const handleInputChange = (setter) => (e) => {
    setter(e.target.value);
  
    // Update image only on first keystroke
    if (!hasTyped) {
      setHasTyped(true);
      setRobotImage(robotImages[1]); // Directly set image here instead of useEffect
    }
  
    // Excitement animation
    if (!isExcited) {
      setIsExcited(true);
      setTimeout(() => setIsExcited(false), 500);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    AuthService.login(username, password)
      .then((data) => {
        console.log("🔐 login response:", data);
        setMessage("Connexion réussie !");
        setRedirect(true);
        setRobotImage(robotImages[1]); // Keep the second image on successful login
        navigate(data.role === "ROLE_ADMIN" ? "/admin/dashboard" : "/welcome"); // Redirect to welcome for regular users
      })
      .catch((error) => {
        setRobotImage(robotImages[2]); // Change to the third image on failed login
        setMessage("vérifiez vos identifiants!");
      });
  };

  if (redirect) {
    return null; // Remove the Hello Admin / User message since we're using navigate
  }

  

  return (
    <AnimatedContainer>
      <div style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
        <Robot3D isExcited={isExcited} robotImage={robotImage} />
        <p>{message}</p>
      </div>
      <form className="form" onSubmit={handleSubmit}>
        <p className="title">{t("login")}</p>
        <p className="message">{t("signupMessage")}</p>

        <label>
          <input
            className="input"
            type="text"
            value={username}
            onChange={handleInputChange(setUsername)}
            required
          />
          <span>{t("username")}</span>
        </label>

        <label>
          <input
            className="input"
            type="password"
            value={password}
            onChange={handleInputChange(setPassword)}
            required
          />
          <span>{t("password")}</span>
        </label>

        <button className="submit">{t("submit")}</button>
        <p className="signin">
          <a href="/">{t("alreadyHaveAccount")}</a>
        </p>
      </form>
    </AnimatedContainer>
  );
};

export default Login;
