import axiosClient from './axiosClient';

/**
 * Menu Item API Service
 * Maps 1:1 with MenuItemController.java
 */
export const menuApi = {
  // 1. Get menu items for a specific restaurant: GET /getRestaurantMenu/{restaurantId} (Public)
  getRestaurantMenu: async (restaurantId) => {
    const response = await axiosClient.get(`/getRestaurantMenu/${restaurantId}`);
    return response.data;
  },

  // 2. Get single menu item by ID: GET /getMenuItem/{Id} (Public)
  getMenuItemById: async (id) => {
    const response = await axiosClient.get(`/getMenuItem/${id}`);
    return response.data;
  },

  // 3. Search menu items by dish name: GET /searchMenuItems?query=... (Public)
  searchMenuItems: async (query = '') => {
    const response = await axiosClient.get('/searchMenuItems', {
      params: { query: query.trim() },
    });
    return response.data;
  },

  // 4. Get menu items by category: GET /getMenuItemsByCategory/{category} (Public)
  getMenuItemsByCategory: async (category) => {
    const response = await axiosClient.get(`/getMenuItemsByCategory/${encodeURIComponent(category)}`);
    return response.data;
  },

  // 5. Get all menu items across restaurants: GET /getAllMenuItems (Public)
  getAllMenuItems: async () => {
    const response = await axiosClient.get('/getAllMenuItems');
    return response.data;
  },
};

