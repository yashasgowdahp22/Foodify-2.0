import axiosClient from './axiosClient';

/**
 * Authentication & Profile API Service
 * Maps 1:1 with AuthController.java and CustomerController.java
 */

export const authApi = {
  // 1. Customer Registration: POST /api/auth/register
  register: async (registerData) => {
    // Body: { name, email, address, password }
    const response = await axiosClient.post('/api/auth/register', registerData);
    return response.data;
  },

  // 2. User Login: POST /api/auth/login
  login: async (credentials) => {
    // Body: { email, password }
    // Returns: { token, id, name, email, role }
    const response = await axiosClient.post('/api/auth/login', credentials);
    return response.data;
  },

  // 3. Stateless Logout: POST /api/auth/logout
  logout: async () => {
    try {
      await axiosClient.post('/api/auth/logout');
    } catch (err) {
      // Even if backend fails or network drops, local token will be removed
      console.warn('Backend logout call failed or network offline:', err);
    }
  },

  // 4. Get Current Authenticated User: GET /api/auth/me
  getCurrentUser: async () => {
    // Returns: UserResponseDTO { id, name, email, address, role }
    const response = await axiosClient.get('/api/auth/me');
    return response.data;
  },

  // 5. Change Password: PUT /api/auth/change-password
  changePassword: async (passwordData) => {
    // Body: { oldPassword, newPassword }
    const response = await axiosClient.put('/api/auth/change-password', passwordData);
    return response.data;
  },

  // 6. Update Customer Profile Details: PUT /updateCustomer/{Id}
  updateProfile: async (id, profileData) => {
    // Body: { name, email, address }
    const response = await axiosClient.put(`/updateCustomer/${id}`, profileData);
    return response.data;
  },
};

