package com.foodify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.model.Cart;
import com.foodify.service.CartService;

@RestController
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping("addItemToCart/{customerId}/{menuItemId}/{quantity}")
	public void addItemToCart(@PathVariable Long customerId,
			@PathVariable Long menuItemId,
			@PathVariable int quantity) {
		cartService.addItemToCart(customerId, menuItemId, quantity);
	}
	@GetMapping("getCart/{customerId}")
	public Cart getCart(@PathVariable Long customerId ) {
		Cart cart=cartService.getCart(customerId);
		return cart;
	}
	
	@PutMapping("updateCartItem/{cartItemId}/{quantity}")
	public void updateCartItem(@PathVariable Long cartItemId,
			@PathVariable int quantity) {
		cartService.updateCartItem(cartItemId,quantity);
	}
	
	@DeleteMapping("deleteCartItem/{cartItemId}")
	public void deleteCartItem(@PathVariable Long cartItemId) {
		cartService.deleteCartItem(cartItemId);
	}
	
	@DeleteMapping("clearCart/{customerId}")
	public void clearCart(@PathVariable Long customerId) {
	    cartService.clearCart(customerId);
	}
	
	@PostMapping("addCart")
	public void addCart(@RequestBody Cart cart) {
		cartService.addCart(cart);
	}

	@GetMapping("getAllCart")
	public List<Cart> getAllCart(){
		return cartService.getAllCart();
	}
	
//	@GetMapping("getCart/{cartId}")
//	public Cart getCartById(@PathVariable Long cartId) {
//		return cartService.getCartById(cartId);
//	}

	@PutMapping("updateCart/{Id}")
	public String updateCart(@RequestBody Cart cart, @PathVariable Long Id) {
		return cartService.updateCart(cart, Id);
	}
	
//	@DeleteMapping("deleteCart/{Id}")
//	public void deleteCart(@PathVariable Long Id) {
//		cartService.deleteCart(Id);
//	}
}