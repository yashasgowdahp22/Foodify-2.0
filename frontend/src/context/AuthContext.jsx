import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';

/**
 * Authentication Context
 * 
 * In Spring Security, the server holds the SecurityContextHolder for the current thread.
 * In a React Single Page Application (SPA), the client holds the user state in Context API.
 * 
 * Features:
 * - Persists JWT token in browser localStorage so page refresh doesn't log the user out.
 * - On initial app load, verifies the stored token by calling GET /api/auth/me.
 * - Provides login, logout, refreshProfile, and updateUser functions to any component via useAuth().
 */

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('foodify_user');
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [token, setToken] = useState(() => {
    return localStorage.getItem('foodify_token') || null;
  });

  const [loading, setLoading] = useState(true);

  // On initial app load: Verify token and sync user profile
  useEffect(() => {
    const verifyUserSession = async () => {
      const storedToken = localStorage.getItem('foodify_token');
      if (storedToken) {
        try {
          const profile = await authApi.getCurrentUser();
          setUser(profile);
          localStorage.setItem('foodify_user', JSON.stringify(profile));
        } catch (error) {
          console.warn('Session expired or invalid token:', error);
          // Token is invalid/expired -> reset state
          localStorage.removeItem('foodify_token');
          localStorage.removeItem('foodify_user');
          setUser(null);
          setToken(null);
        }
      }
      setLoading(false);
    };

    verifyUserSession();

    // Listen for session expiration events dispatched by axios interceptor
    const handleAuthExpired = () => {
      setToken(null);
      setUser(null);
    };
    window.addEventListener('foodify:auth-expired', handleAuthExpired);
    return () => window.removeEventListener('foodify:auth-expired', handleAuthExpired);
  }, []);

  // Login handler
  const login = async (email, password) => {
    // 1. Authenticate with backend: POST /api/auth/login
    const data = await authApi.login({ email, password });
    
    // 2. Set token in memory & storage so subsequent calls include Authorization header
    setToken(data.token);
    localStorage.setItem('foodify_token', data.token);

    // 3. Immediately fetch full profile (contains address) from GET /api/auth/me
    try {
      const profile = await authApi.getCurrentUser();
      setUser(profile);
      localStorage.setItem('foodify_user', JSON.stringify(profile));
      return profile;
    } catch (err) {
      // Fallback to login response data if profile fetch has an issue
      const fallbackUser = {
        id: data.id,
        name: data.name,
        email: data.email,
        role: data.role,
        address: '',
      };
      setUser(fallbackUser);
      localStorage.setItem('foodify_user', JSON.stringify(fallbackUser));
      return fallbackUser;
    }
  };

  // Logout handler
  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      setToken(null);
      setUser(null);
      localStorage.removeItem('foodify_token');
      localStorage.removeItem('foodify_user');
    }
  };

  // Helper to re-fetch the latest profile from the backend
  const refreshProfile = async () => {
    try {
      const profile = await authApi.getCurrentUser();
      setUser(profile);
      localStorage.setItem('foodify_user', JSON.stringify(profile));
      return profile;
    } catch (error) {
      console.warn('Failed to refresh profile:', error);
      throw error;
    }
  };

  // Helper to update local profile after profile edit
  const updateUser = (updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem('foodify_user', JSON.stringify(updatedUser));
  };

  const value = {
    user,
    token,
    isAuthenticated: !!token,
    isCustomer: user?.role === 'CUSTOMER',
    loading,
    login,
    logout,
    refreshProfile,
    updateUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {loading ? (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
          <div className="relative w-14 h-14">
            <div className="w-14 h-14 rounded-full border-4 border-orange-100 border-t-brand-500 animate-spin"></div>
          </div>
          <p className="mt-4 text-sm font-semibold text-gray-500 animate-pulse">Initializing Foodify 2.0...</p>
        </div>
      ) : (
        children
      )}
    </AuthContext.Provider>
  );
};

// Custom hook for convenient access in any component
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
