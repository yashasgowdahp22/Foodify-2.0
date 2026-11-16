package com.foodify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.dto.AddToCartRequest;
import com.foodify.dto.CartResponseDTO;
import com.foodify.model.Cart;
import com.foodify.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Cart Management", description = "Cart operations for Customers and Admin")
@RestController
public class CartController {

	@Autowired
	private CartService cartService;

	// ==================== NEW PHASE 3 ENDPOINTS ====================

	@Operation(summary = "Get cart for currently logged-in customer")
	@GetMapping("myCart")
	public ResponseEntity<CartResponseDTO> getMyCart(Authentication authentication) {
		String customerEmail = authentication.getName();
		CartResponseDTO cartResponse = cartService.getMyCart(customerEmail);
		return ResponseEntity.ok(cartResponse);
	}

	@Operation(summary = "Add item to cart for currently logged-in customer")
	@PostMapping("myCart/items")
	public ResponseEntity<CartResponseDTO> addItemToMyCart(
			Authentication authentication,
			@Valid @RequestBody AddToCartRequest request) {
		String customerEmail = authentication.getName();
		CartResponseDTO cartResponse = cartService.addItemToMyCart(
				customerEmail,
				request.getMenuItemId(),
				request.getQuantity());
		return ResponseEntity.ok(cartResponse);
	}

	@Operation(summary = "Update quantity of item in currently logged-in customer's cart")
	@PutMapping("myCart/items/{cartItemId}/{quantity}")
	public ResponseEntity<CartResponseDTO> updateMyCartItem(
			Authentication authentication,
			@PathVariable Long cartItemId,
			@PathVariable int quantity) {
		String customerEmail = authentication.getName();
		CartResponseDTO cartResponse = cartService.updateMyCartItem(customerEmail, cartItemId, quantity);
		return ResponseEntity.ok(cartResponse);
	}

	@Operation(summary = "Remove item from currently logged-in customer's cart")
	@DeleteMapping("myCart/items/{cartItemId}")
	public ResponseEntity<CartResponseDTO> deleteMyCartItem(
			Authentication authentication,
			@PathVariable Long cartItemId) {
		String customerEmail = authentication.getName();
		CartResponseDTO cartResponse = cartService.deleteMyCartItem(customerEmail, cartItemId);
		return ResponseEntity.ok(cartResponse);
	}

	@Operation(summary = "Clear all items from currently logged-in customer's cart")
	@DeleteMapping("myCart")
	public ResponseEntity<CartResponseDTO> clearMyCart(Authentication authentication) {
		String customerEmail = authentication.getName();
		CartResponseDTO cartResponse = cartService.clearMyCart(customerEmail);
		return ResponseEntity.ok(cartResponse);
	}

	// ==================== PRESERVED EXISTING ENDPOINTS ====================

	@PostMapping("addItemToCart/{customerId}/{menuItemId}/{quantity}")
	public void addItemToCart(@PathVariable Long customerId,
			@PathVariable Long menuItemId,
			@PathVariable int quantity,
			Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			if (!isAdmin) {
				cartService.verifyCustomerCartOwnership(customerId, authentication.getName());
			}
		}
		cartService.addItemToCart(customerId, menuItemId, quantity);
	}

	@GetMapping("getCart/{customerId}")
	public Cart getCart(@PathVariable Long customerId, Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			if (!isAdmin) {
				cartService.verifyCustomerCartOwnership(customerId, authentication.getName());
			}
		}
		Cart cart = cartService.getCart(customerId);
		return cart;
	}
	
	@PutMapping("updateCartItem/{cartItemId}/{quantity}")
	public void updateCartItem(@PathVariable Long cartItemId,
			@PathVariable int quantity) {
		cartService.updateCartItem(cartItemId, quantity);
	}
	
	@DeleteMapping("deleteCartItem/{cartItemId}")
	public void deleteCartItem(@PathVariable Long cartItemId) {
		cartService.deleteCartItem(cartItemId);
	}
	
	@DeleteMapping("clearCart/{customerId}")
	public void clearCart(@PathVariable Long customerId, Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			if (!isAdmin) {
				cartService.verifyCustomerCartOwnership(customerId, authentication.getName());
			}
		}
	    cartService.clearCart(customerId);
	}
	
	@PostMapping("addCart")
	public void addCart(@RequestBody Cart cart) {
		cartService.addCart(cart);
	}

	@GetMapping("getAllCart")
	public List<Cart> getAllCart() {
		List<Cart> carts = cartService.getAllCart();
		return carts;
	}

	@PutMapping("updateCart/{Id}")
	public String updateCart(@RequestBody Cart cart, @PathVariable Long Id) {
		String result = cartService.updateCart(cart, Id);
		return result;
	}
}