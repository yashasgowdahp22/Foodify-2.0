import axios from 'axios';

/**
 * Centralized Axios HTTP Client
 * 
 * Think of this like a Spring WebClient or RestTemplate configured with an Interceptor:
 * 1. Base URL points to the Spring Boot backend (http://localhost:8080).
 * 2. Request Interceptor: Automatically attaches the JWT Bearer token to headers if logged in.
 * 3. Response Interceptor: Catches 401 Unauthorized errors (e.g. expired token) and clears local storage.
 */

// Read base URL from environment variables, fallback to localhost:8080
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const axiosClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Add JWT token to every outgoing request
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('foodify_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Handle errors globally (e.g., token expired / 401)
axiosClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear invalid credentials from browser storage
      localStorage.removeItem('foodify_token');
      localStorage.removeItem('foodify_user');
      // Notify application of expired session
      window.dispatchEvent(new Event('foodify:auth-expired'));
    }
    return Promise.reject(error);
  }
);

export default axiosClient;

