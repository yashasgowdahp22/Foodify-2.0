import axiosClient from './axiosClient';

/**
 * Customer Cart API Service
 * Maps 1:1 with CartController.java (New Phase 3 / Customer Endpoints)
 */
export const cartApi = {
  // 1. Get current logged-in customer's cart: GET /myCart
  getMyCart: async () => {
    const response = await axiosClient.get('/myCart');
    return response.data;
  },

  // 2. Add item to cart: POST /myCart/items
  // Payload: { menuItemId: Long, quantity: int }
  addToCart: async (menuItemId, quantity = 1) => {
    const response = await axiosClient.post('/myCart/items', {
      menuItemId: Number(menuItemId),
      quantity: Number(quantity),
    });
    return response.data;
  },

  // 3. Update quantity of item in cart: PUT /myCart/items/{cartItemId}/{quantity}
  updateCartItemQuantity: async (cartItemId, quantity) => {
    const response = await axiosClient.put(`/myCart/items/${cartItemId}/${quantity}`);
    return response.data;
  },

  // 4. Remove item from cart: DELETE /myCart/items/{cartItemId}
  removeCartItem: async (cartItemId) => {
    const response = await axiosClient.delete(`/myCart/items/${cartItemId}`);
    return response.data;
  },

  // 5. Clear all items from cart: DELETE /myCart
  clearCart: async () => {
    const response = await axiosClient.delete('/myCart');
    return response.data;
  },
};

