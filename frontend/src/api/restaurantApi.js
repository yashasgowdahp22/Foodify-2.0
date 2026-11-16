import axiosClient from './axiosClient';

/**
 * Restaurant API Service
 * Maps 1:1 with RestaurantController.java
 */
export const restaurantApi = {
  // 1. Get all restaurants: GET /getAllRestaurants (Public)
  getAllRestaurants: async () => {
    const response = await axiosClient.get('/getAllRestaurants');
    return response.data;
  },

  // 2. Get restaurant details by ID: GET /getRestaurant/{Id} (Public)
  getRestaurantById: async (id) => {
    const response = await axiosClient.get(`/getRestaurant/${id}`);
    return response.data;
  },

  // 3. Search restaurants by name or address: GET /searchRestaurants?query=... (Public)
  searchRestaurants: async (query = '') => {
    const response = await axiosClient.get('/searchRestaurants', {
      params: { query: query.trim() },
    });
    return response.data;
  },
};

