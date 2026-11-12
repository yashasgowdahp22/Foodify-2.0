package com.foodify.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	@Transactional
	public void addItemToCart(Long customerId,Long menuItemId,int quantity) {

		if(quantity<=0) {
			throw new RuntimeException("Quantity must be Atleast 1 or More Than One");
		}

		Customer customer=customerRepository.findById(customerId)
				.orElseThrow(()-> new RuntimeException("Customer is not found"));

		Cart cart = customer.getCart();		

		if(cart==null) {
			cart=new Cart();
			customer.setCart(cart);
			cart.setCustomer(customer);
		}


		MenuItem menuItem=menuItemRepository.findById(menuItemId)
				.orElseThrow(()-> new RuntimeException("MenuItem is not found"));

		if(!menuItem.isAvailable()) {
			throw new RuntimeException("MenuItem is not available");
		}

		if(!cart.getCartItems().isEmpty()) {
			Long existingRestaurantId=cart.getCartItems().get(0)
					.getMenuItem().getRestaurant().getRestaurantId();

			Long selectedRestaurantId=menuItem.getRestaurant().getRestaurantId();  	

			if(!existingRestaurantId.equals(selectedRestaurantId)) {
				throw new RuntimeException("You can only add items from one restaurant");
			}
		}


		CartItem existCartItem=null;

		for (CartItem item : cart.getCartItems()) {
			if (item.getMenuItem().getId().equals(menuItemId)) {
				existCartItem = item;
				break;
			}
		}
		if(existCartItem!=null) {
			existCartItem.setQuantity(existCartItem.getQuantity()+quantity);
		}
		else {
			CartItem newcartItem=new CartItem();
			newcartItem.setQuantity(quantity);
			newcartItem.setPrice(menuItem.getPrice());
			newcartItem.setMenuItem(menuItem);
			cart.addCartItems(newcartItem);
		}
		cartRepository.save(cart);
	}
	
//	@Transactional
	public Cart getCart(Long customerId) {

		Customer customer=customerRepository.findById(customerId)
				.orElseThrow(()-> new RuntimeException ("Customer not found"));

		Cart cart=customer.getCart();
		if(cart==null) {
			throw new RuntimeException("cart is empty");
		}
		return cart;
	}

	@Transactional
	public void updateCartItem(Long cartItemId,int quantity) {
		
		if(quantity <= 0) {
			throw new RuntimeException("quantity must be greater than 0");
		}
		
		CartItem cartItem=cartItemRepository.findById(cartItemId)
				.orElseThrow(()-> new RuntimeException("CartItem not found"));

		cartItem.setQuantity(quantity);

		cartItemRepository.save(cartItem);
	}

	@Transactional
	public void deleteCartItem(Long cartItemId) {

		CartItem cartItem=cartItemRepository.findById(cartItemId)
		.orElseThrow(()-> new RuntimeException("CartItem not found"));
		
		Cart cart=cartItem.getCart();

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

	public List<Cart> getAllCart(){
		List<Cart> cart = cartRepository.findAll();
		return cart;
	}

	//	public Cart getCartById(Long Id) {
	//		return cartRepository.findById(Id)
	//				.orElseThrow(() -> new RuntimeException ("Cart not found with this Id"+Id));
	//	}
	public String updateCart(Cart cart, Long Id) {
		if(cartRepository.existsById(Id)) {
			cartRepository.save(cart);
			return "Cart found";
		}
		else {
			return "Cart Not found";
		}
	}
	public void deleteCart(Long id) {
		cartRepository.deleteById(id);
	}

	

	

}
