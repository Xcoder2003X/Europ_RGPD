import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth/';  // Mets à jour cette URL selon ton serveur Spring Boot

const register = (username, password, role) => {
  return axios.post(API_URL + 'signup', { username, password, role });
};

const login = async (username, password) => {
  try {
    const response = await axios.post("http://localhost:8080/api/auth/signin", {
      username,
      password,
    });
    
    if (response.data.token) {
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("user", JSON.stringify(response.data));
    }
    
    return response.data;
  } catch (error) {
    throw error;
  }
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
};

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem("user"));
};

export default {
  register,
  login,
  logout,
  getCurrentUser
};
