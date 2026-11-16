package com.foodify.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.dto.CartItemResponseDTO;
import com.foodify.dto.CartResponseDTO;
import com.foodify.exception.BadRequestException;
import com.foodify.exception.ResourceNotFoundException;
import com.foodify.exception.UnauthorizedException;
import com.foodify.model.Cart;
import com.foodify.model.CartItem;
import com.foodify.model.Customer;
import com.foodify.model.MenuItem;
import com.foodify.repository.CartItemRepository;
import com.foodify.repository.CartRepository;
import com.foodify.repository.CustomerRepository;
import com.foodify.repository.MenuItemRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	@Autowired
	private CartItemRepository cartItemRepository;

	// ==================== NEW PHASE 3 ENDPOINT SERVICES ====================

	public CartResponseDTO getMyCart(String customerEmail) {
		Customer customer = customerRepository.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + customerEmail));

		Cart cart = customer.getCart();
		CartResponseDTO response = mapToCartResponseDTO(cart, customer);
		return response;
	}

	@Transactional
	public CartResponseDTO addItemToMyCart(String customerEmail, Long menuItemId, int quantity) {
		if (quantity <= 0) {
			throw new BadRequestException("Quantity must be at least 1");
		}

		Customer customer = customerRepository.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + customerEmail));

		MenuItem menuItem = menuItemRepository.findById(menuItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuItemId));

		if (!menuItem.isAvailable()) {
			throw new BadRequestException("Menu item '" + menuItem.getName() + "' is not available");
		}

		if (menuItem.getRestaurant() != null && !menuItem.getRestaurant().isOpen()) {
			throw new BadRequestException("Restaurant '" + menuItem.getRestaurant().getRestaurantName() + "' is currently closed");
		}

		Cart cart = customer.getCart();
		if (cart == null) {
			cart = new Cart();
			cart.setCustomer(customer);
			cart.setCreatedAt(LocalDateTime.now());
			cart.setUpdatedAt(LocalDateTime.now());
			cart.setStatus("ACTIVE");
			customer.setCart(cart);
		}

		if (!cart.getCartItems().isEmpty()) {
			CartItem firstItem = cart.getCartItems().get(0);
			Long existingRestaurantId = firstItem.getMenuItem().getRestaurant().getRestaurantId();
			Long selectedRestaurantId = menuItem.getRestaurant().getRestaurantId();

			if (!existingRestaurantId.equals(selectedRestaurantId)) {
				throw new BadRequestException("You can only add items from one restaurant at a time. Clear your cart to order from another restaurant.");
			}
		}

		CartItem existingCartItem = null;
		for (CartItem item : cart.getCartItems()) {
			if (item.getMenuItem().getId().equals(menuItemId)) {
				existingCartItem = item;
				break;
			}
		}

		if (existingCartItem != null) {
			int newQuantity = existingCartItem.getQuantity() + quantity;
			existingCartItem.setQuantity(newQuantity);
			existingCartItem.setPrice(menuItem.getPrice());
		} else {
			CartItem newCartItem = new CartItem();
			newCartItem.setQuantity(quantity);
			newCartItem.setPrice(menuItem.getPrice());
			newCartItem.setMenuItem(menuItem);
			cart.addCartItems(newCartItem);
		}

		cart.setUpdatedAt(LocalDateTime.now());
		Cart savedCart = cartRepository.save(cart);

		CartResponseDTO response = mapToCartResponseDTO(savedCart, customer);
		return response;
	}

	@Transactional
	public CartResponseDTO updateMyCartItem(String customerEmail, Long cartItemId, int quantity) {
		if (quantity <= 0) {
			throw new BadRequestException("Quantity must be at least 1");
		}

		Customer customer = customerRepository.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + customerEmail));

		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

		if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
			throw new UnauthorizedException("You are not authorized to modify this cart item");
		}

		cartItem.setQuantity(quantity);
		cartItemRepository.save(cartItem);

		Cart cart = cartItem.getCart();
		cart.setUpdatedAt(LocalDateTime.now());
		Cart savedCart = cartRepository.save(cart);

		CartResponseDTO response = mapToCartResponseDTO(savedCart, customer);
		return response;
	}

	@Transactional
	public CartResponseDTO deleteMyCartItem(String customerEmail, Long cartItemId) {
		Customer customer = customerRepository.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + customerEmail));

		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

		if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
			throw new UnauthorizedException("You are not authorized to delete this cart item");
		}

		Cart cart = cartItem.getCart();
		cart.removeCartItem(cartItem);
		cart.setUpdatedAt(LocalDateTime.now());
		Cart savedCart = cartRepository.save(cart);

		CartResponseDTO response = mapToCartResponseDTO(savedCart, customer);
		return response;
	}

	@Transactional
	public CartResponseDTO clearMyCart(String customerEmail) {
		Customer customer = customerRepository.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + customerEmail));

		Cart cart = customer.getCart();
		if (cart != null) {
			cart.getCartItems().clear();
			cart.setUpdatedAt(LocalDateTime.now());
			Cart savedCart = cartRepository.save(cart);
			CartResponseDTO response = mapToCartResponseDTO(savedCart, customer);
			return response;
		}

		CartResponseDTO emptyResponse = mapToCartResponseDTO(null, customer);
		return emptyResponse;
	}

	public CartResponseDTO mapToCartResponseDTO(Cart cart, Customer customer) {
		CartResponseDTO response = new CartResponseDTO();
		response.setCustomerId(customer.getId());
		response.setCustomerName(customer.getName());

		if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
			response.setCartId(cart != null ? cart.getId() : null);
			response.setRestaurantId(null);
			response.setRestaurantName(null);
			response.setTotalAmount(BigDecimal.ZERO);
			response.setItemCount(0);
			response.setItems(new ArrayList<>());
			return response;
		}

		response.setCartId(cart.getId());

		List<CartItemResponseDTO> itemDTOList = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;
		int totalQuantity = 0;

		for (CartItem item : cart.getCartItems()) {
			MenuItem menuItem = item.getMenuItem();
			BigDecimal price = item.getPrice();
			int qty = item.getQuantity();
			BigDecimal subTotal = price.multiply(BigDecimal.valueOf(qty));

			CartItemResponseDTO itemDTO = new CartItemResponseDTO();
			itemDTO.setCartItemId(item.getId());
			itemDTO.setMenuItemId(menuItem.getId());
			itemDTO.setMenuItemName(menuItem.getName());
			itemDTO.setPrice(price);
			itemDTO.setQuantity(qty);
			itemDTO.setSubTotal(subTotal);
			itemDTO.setCategory(menuItem.getCategory());
			itemDTO.setImageUrl(menuItem.getImageUrl());
			itemDTO.setAvailable(menuItem.isAvailable());

			itemDTOList.add(itemDTO);

			totalAmount = totalAmount.add(subTotal);
			totalQuantity = totalQuantity + qty;
		}

		CartItem firstItem = cart.getCartItems().get(0);
		if (firstItem.getMenuItem() != null && firstItem.getMenuItem().getRestaurant() != null) {
			response.setRestaurantId(firstItem.getMenuItem().getRestaurant().getRestaurantId());
			response.setRestaurantName(firstItem.getMenuItem().getRestaurant().getRestaurantName());
		}

		response.setTotalAmount(totalAmount);
		response.setItemCount(totalQuantity);
		response.setItems(itemDTOList);

		return response;
	}

	public void verifyCustomerCartOwnership(Long customerId, String email) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
		if (!customer.getEmail().equalsIgnoreCase(email)) {
			throw new UnauthorizedException("You are not authorized to access another customer's cart");
		}
	}

	// ==================== EXISTING METHODS PRESERVED ====================

	@Transactional
	public void addItemToCart(Long customerId, Long menuItemId, int quantity) {

		if (quantity <= 0) {
			throw new RuntimeException("Quantity must be Atleast 1 or More Than One");
		}

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer is not found"));

		Cart cart = customer.getCart();		

		if (cart == null) {
			cart = new Cart();
			customer.setCart(cart);
			cart.setCustomer(customer);
		}

		MenuItem menuItem = menuItemRepository.findById(menuItemId)
				.orElseThrow(() -> new RuntimeException("MenuItem is not found"));

		if (!menuItem.isAvailable()) {
			throw new RuntimeException("MenuItem is not available");
		}

		if (menuItem.getRestaurant() != null && !menuItem.getRestaurant().isOpen()) {
			throw new BadRequestException("Restaurant is currently closed");
		}

		if (!cart.getCartItems().isEmpty()) {
			Long existingRestaurantId = cart.getCartItems().get(0)
					.getMenuItem().getRestaurant().getRestaurantId();

			Long selectedRestaurantId = menuItem.getRestaurant().getRestaurantId();  	

			if (!existingRestaurantId.equals(selectedRestaurantId)) {
				throw new RuntimeException("You can only add items from one restaurant");
			}
		}

		CartItem existCartItem = null;

		for (CartItem item : cart.getCartItems()) {
			if (item.getMenuItem().getId().equals(menuItemId)) {
				existCartItem = item;
				break;
			}
		}
		if (existCartItem != null) {
			existCartItem.setQuantity(existCartItem.getQuantity() + quantity);
		} else {
			CartItem newcartItem = new CartItem();
			newcartItem.setQuantity(quantity);
			newcartItem.setPrice(menuItem.getPrice());
			newcartItem.setMenuItem(menuItem);
			cart.addCartItems(newcartItem);
		}
		cartRepository.save(cart);
	}
	
	public Cart getCart(Long customerId) {

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		Cart cart = customer.getCart();
		if (cart == null) {
			throw new RuntimeException("cart is empty");
		}
		return cart;
	}

	@Transactional
	public void updateCartItem(Long cartItemId, int quantity) {
		
		if (quantity <= 0) {
			throw new RuntimeException("quantity must be greater than 0");
		}
		
		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new RuntimeException("CartItem not found"));

		cartItem.setQuantity(quantity);

		cartItemRepository.save(cartItem);
	}

	@Transactional
	public void deleteCartItem(Long cartItemId) {

		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new RuntimeException("CartItem not found"));
		
		Cart cart = cartItem.getCart();

		cart.removeCartItem(cartItem);
	}

	@Transactional
	public void clearCart(Long customerId) {

	    Cart cart = cartRepository.findByCustomerId(customerId)
	            .orElseThrow(() -> new RuntimeException("Cart not found"));

	    cart.getCartItems().clear();
	}

	public void addCart(Cart cart) {
		cartRepository.save(cart);
	}

	public List<Cart> getAllCart() {
		List<Cart> cart = cartRepository.findAll();
		return cart;
	}

	public String updateCart(Cart cart, Long Id) {
		if (cartRepository.existsById(Id)) {
			cartRepository.save(cart);
			return "Cart found";
		} else {
			return "Cart Not found";
		}
	}

	public void deleteCart(Long id) {
		cartRepository.deleteById(id);
	}
}
