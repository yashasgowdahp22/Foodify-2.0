import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { cartApi } from '../api/cartApi';
import { useAuth } from './AuthContext';

/**
 * Customer Cart Context
 * 
 * Manages:
 * 1. Active Cart state (cartId, items, restaurantId, restaurantName, totalAmount, itemCount).
 * 2. Add / Update / Remove / Clear cart operations mapped to Spring Boot CartController.
 * 3. Enforces the one-restaurant-per-cart restriction with a user confirmation modal.
 * 4. Syncs dynamically with Navbar badge counter.
 */

const CartContext = createContext(null);

const EMPTY_CART = {
  cartId: null,
  customerId: null,
  customerName: null,
  restaurantId: null,
  restaurantName: null,
  totalAmount: 0,
  itemCount: 0,
  items: [],
};

export const CartProvider = ({ children }) => {
  const { isAuthenticated, isCustomer } = useAuth();

  const [cart, setCart] = useState(EMPTY_CART);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // State for the one-restaurant-per-cart conflict modal
  const [conflictModal, setConflictModal] = useState({
    isOpen: false,
    existingRestaurantName: '',
    newRestaurantName: '',
    pendingItem: null,
    pendingQuantity: 1,
  });

  // Fetch cart from backend: GET /myCart
  const fetchCart = useCallback(async () => {
    if (!isAuthenticated || !isCustomer) {
      setCart(EMPTY_CART);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const data = await cartApi.getMyCart();
      setCart(data || EMPTY_CART);
    } catch (err) {
      console.warn('Could not load cart from backend:', err);
      // If error occurs, keep empty state
      setCart(EMPTY_CART);
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, isCustomer]);

  // Sync cart on initial mount or when login status changes
  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  // Helper to find an item in cart
  const getCartItem = useCallback((menuItemId) => {
    if (!cart?.items || cart.items.length === 0) return null;
    const targetId = Number(menuItemId);
    return cart.items.find((item) => Number(item.menuItemId) === targetId) || null;
  }, [cart]);

  // Helper to get quantity of a dish in cart
  const getItemQuantity = useCallback((menuItemId) => {
    const item = getCartItem(menuItemId);
    return item ? item.quantity : 0;
  }, [getCartItem]);

  // 1. Add item to cart
  const addToCart = async (menuItem, quantity = 1) => {
    if (!isAuthenticated) {
      return { success: false, requiresAuth: true };
    }

    const currentRestaurantId = cart?.restaurantId ? Number(cart.restaurantId) : null;
    const targetRestaurantId = menuItem?.restaurantId ? Number(menuItem.restaurantId) : null;
    const hasItemsFromOtherRestaurant =
      currentRestaurantId &&
      targetRestaurantId &&
      currentRestaurantId !== targetRestaurantId &&
      cart.itemCount > 0;

    // Check one-restaurant-per-cart restriction
    if (hasItemsFromOtherRestaurant) {
      setConflictModal({
        isOpen: true,
        existingRestaurantName: cart.restaurantName || 'Current Restaurant',
        newRestaurantName: menuItem.restaurantName || 'New Restaurant',
        pendingItem: menuItem,
        pendingQuantity: quantity,
      });
      return { success: false, conflict: true };
    }

    try {
      setLoading(true);
      setError(null);
      const updatedCart = await cartApi.addToCart(menuItem.id, quantity);
      setCart(updatedCart || EMPTY_CART);
      return { success: true, cart: updatedCart };
    } catch (err) {
      console.error('Error adding to cart:', err);
      const msg = err.response?.data?.message || 'Failed to add item to cart';
      
      // Also catch if backend throws restaurant conflict error (HTTP 400)
      if (msg.toLowerCase().includes('one restaurant') || msg.toLowerCase().includes('another restaurant')) {
        setConflictModal({
          isOpen: true,
          existingRestaurantName: cart.restaurantName || 'Current Restaurant',
          newRestaurantName: menuItem.restaurantName || 'New Restaurant',
          pendingItem: menuItem,
          pendingQuantity: quantity,
        });
        return { success: false, conflict: true };
      }

      setError(msg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // 2. Confirm replacing cart with new restaurant's item
  const confirmReplaceCart = async () => {
    const { pendingItem, pendingQuantity } = conflictModal;
    if (!pendingItem) return;

    try {
      setLoading(true);
      setError(null);
      // Step A: Clear existing cart
      await cartApi.clearCart();
      // Step B: Add new restaurant's item
      const updatedCart = await cartApi.addToCart(pendingItem.id, pendingQuantity || 1);
      setCart(updatedCart || EMPTY_CART);
      
      // Close modal
      setConflictModal({
        isOpen: false,
        existingRestaurantName: '',
        newRestaurantName: '',
        pendingItem: null,
        pendingQuantity: 1,
      });

      return { success: true, cart: updatedCart };
    } catch (err) {
      console.error('Error replacing cart:', err);
      setError('Failed to replace cart. Please try again.');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // Close conflict modal without making changes
  const closeConflictModal = () => {
    setConflictModal({
      isOpen: false,
      existingRestaurantName: '',
      newRestaurantName: '',
      pendingItem: null,
      pendingQuantity: 1,
    });
  };

  // 3. Update cart item quantity
  const updateQuantity = async (cartItemId, newQuantity) => {
    if (newQuantity <= 0) {
      return removeCartItem(cartItemId);
    }

    try {
      setLoading(true);
      setError(null);
      const updatedCart = await cartApi.updateCartItemQuantity(cartItemId, newQuantity);
      setCart(updatedCart || EMPTY_CART);
      return updatedCart;
    } catch (err) {
      console.error('Error updating cart item quantity:', err);
      const msg = err.response?.data?.message || 'Failed to update item quantity';
      setError(msg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // 4. Remove individual cart item
  const removeCartItem = async (cartItemId) => {
    try {
      setLoading(true);
      setError(null);
      const updatedCart = await cartApi.removeCartItem(cartItemId);
      setCart(updatedCart || EMPTY_CART);
      return updatedCart;
    } catch (err) {
      console.error('Error removing cart item:', err);
      const msg = err.response?.data?.message || 'Failed to remove item from cart';
      setError(msg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  // 5. Clear all items from cart
  const clearCart = async () => {
    try {
      setLoading(true);
      setError(null);
      const updatedCart = await cartApi.clearCart();
      setCart(updatedCart || EMPTY_CART);
      return updatedCart;
    } catch (err) {
      console.error('Error clearing cart:', err);
      const msg = err.response?.data?.message || 'Failed to clear cart';
      setError(msg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const value = {
    cart,
    loading,
    error,
    conflictModal,
    fetchCart,
    addToCart,
    updateQuantity,
    removeCartItem,
    clearCart,
    confirmReplaceCart,
    closeConflictModal,
    getCartItem,
    getItemQuantity,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

