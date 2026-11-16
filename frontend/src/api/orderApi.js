import axiosClient from './axiosClient';

/**
 * Order & Checkout API Service
 * 
 * Maps to Spring Boot OrderController endpoints:
 * - POST /checkout               -> Checkout cart and place an order (Customer)
 * - GET  /myOrders               -> Customer order history
 * - GET  /myOrders/{orderId}     -> Specific customer order receipt
 * - PUT  /myOrders/{orderId}/cancel -> Cancel pending order
 */
export const orderApi = {
  /**
   * Checkout customer's cart and place order
   * @param {Object} checkoutData - { deliveryAddress, specialInstructions, paymentMethod }
   * @returns {Promise<Object>} OrderResponseDTO
   */
  checkout: async (checkoutData) => {
    const response = await axiosClient.post('/checkout', checkoutData);
    return response.data;
  },

  /**
   * Get all orders placed by the currently logged-in customer
   * @returns {Promise<Array>} List of OrderResponseDTO
   */
  getMyOrders: async () => {
    const response = await axiosClient.get('/myOrders');
    return response.data;
  },

  /**
   * Get specific order details by ID for the logged-in customer
   * @param {number|string} orderId
   * @returns {Promise<Object>} OrderResponseDTO
   */
  getMyOrderById: async (orderId) => {
    const response = await axiosClient.get(`/myOrders/${orderId}`);
    return response.data;
  },

  /**
   * Cancel an order (only permitted if order status is PENDING)
   * @param {number|string} orderId
   * @returns {Promise<Object>} OrderResponseDTO
   */
  cancelMyOrder: async (orderId) => {
    const response = await axiosClient.put(`/myOrders/${orderId}/cancel`);
    return response.data;
  },
};

export default orderApi;

