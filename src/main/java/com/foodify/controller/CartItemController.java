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

import com.foodify.model.CartItem;
import com.foodify.service.CartItemService;

@RestController
public class CartItemController {

	@Autowired
	private CartItemService cartItemService;
	
	@PostMapping("addCartItem")
	public void addCartItem(@RequestBody CartItem cartItem) {
		cartItemService.addCartItem(cartItem);
	}
	
	@GetMapping("getAllCartItem")
	public List<CartItem> getAllCartItem(){
		return cartItemService.getAllCartItem();
	}
	
	@GetMapping("getCartItem/{Id}")
	public CartItem getCartItemById(@PathVariable Long Id) {
		return cartItemService.getCartItemById(Id);
	}
	
	@PutMapping("updateCartItem/{Id}")
	public String updateCartItem(@RequestBody CartItem cartItem, @PathVariable Long Id) {
		return cartItemService.updateCartItem(cartItem, Id);
	}
	
	@DeleteMapping("deleteCartItemById/{Id}")
	public void deleteCartItem(@PathVariable Long Id) {
		cartItemService.deleteCartItem(Id);
	}
}