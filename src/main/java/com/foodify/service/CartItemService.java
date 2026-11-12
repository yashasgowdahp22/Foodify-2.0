package com.foodify.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.model.CartItem;
import com.foodify.repository.CartItemRepository;
import com.foodify.repository.CartRepository;

@Service
public class CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;



	public void addCartItem(CartItem cartItem) {
		cartItemRepository.save(cartItem);
	}


	public List<CartItem> getAllCartItem(){
		List<CartItem> cartItem = cartItemRepository.findAll();
		return cartItem;
	}

	public CartItem getCartItemById(Long id) {
		return cartItemRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("CartItem not found with id: " + id));
	}
	
	public String updateCartItem(CartItem cartItem,Long Id) {

		if(cartItemRepository.existsById(Id)) {
			cartItemRepository.save(cartItem);
			return "CartItem updated";
		}

		return "CartItem not found";
	}

	public void deleteCartItem(Long id) {
		cartItemRepository.deleteById(id);
	}


}
