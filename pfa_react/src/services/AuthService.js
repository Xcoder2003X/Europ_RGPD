import axios from 'axios';

const API_URL = 'http://localhost/api/auth/';

const register = (username, password, role) => {
  return axios.post(API_URL + 'signup', { username, password, role });
};

const login = async (username, password) => {
  // call the Spring /signin endpoint
  const response = await axios.post(API_URL + 'signin', { username, password });

  // if we got a token back, stash it
  if (response.data.token) {
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('user', JSON.stringify(response.data)); 
  }

  // this is the crucial bit—you must return the payload
  return response.data;
};

const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem('user'));
};

export default {
  register,
  login,
  logout,
  getCurrentUser
};

