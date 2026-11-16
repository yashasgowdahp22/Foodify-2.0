package com.foodify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.dto.CheckoutRequestDTO;
import com.foodify.dto.OrderResponseDTO;
import com.foodify.model.Order;
import com.foodify.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Order Management", description = "Order and Checkout APIs for Customers, Restaurant Owners, and Admin")
@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	// ==================== NEW PHASE 3 ENDPOINTS ====================

	@Operation(summary = "Checkout cart and place an order (Customer)")
	@PostMapping("checkout")
	public ResponseEntity<OrderResponseDTO> checkout(
			Authentication authentication,
			@Valid @RequestBody CheckoutRequestDTO request) {
		String customerEmail = authentication.getName();
		OrderResponseDTO response = orderService.checkout(customerEmail, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Get order history for currently logged-in customer")
	@GetMapping("myOrders")
	public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Authentication authentication) {
		String customerEmail = authentication.getName();
		List<OrderResponseDTO> orders = orderService.getMyOrders(customerEmail);
		return ResponseEntity.ok(orders);
	}

	@Operation(summary = "Get specific order details for currently logged-in customer")
	@GetMapping("myOrders/{orderId}")
	public ResponseEntity<OrderResponseDTO> getMyOrderById(
			Authentication authentication,
			@PathVariable Long orderId) {
		String customerEmail = authentication.getName();
		OrderResponseDTO order = orderService.getMyOrderById(customerEmail, orderId);
		return ResponseEntity.ok(order);
	}

	@Operation(summary = "Cancel an order (Customer - only PENDING orders)")
	@PutMapping("myOrders/{orderId}/cancel")
	public ResponseEntity<OrderResponseDTO> cancelMyOrder(
			Authentication authentication,
			@PathVariable Long orderId) {
		String customerEmail = authentication.getName();
		OrderResponseDTO order = orderService.cancelMyOrder(customerEmail, orderId);
		return ResponseEntity.ok(order);
	}

	@Operation(summary = "Get incoming orders for currently logged-in restaurant owner")
	@GetMapping("restaurant/orders")
	public ResponseEntity<List<OrderResponseDTO>> getRestaurantOrders(Authentication authentication) {
		String ownerEmail = authentication.getName();
		List<OrderResponseDTO> orders = orderService.getRestaurantOrders(ownerEmail);
		return ResponseEntity.ok(orders);
	}

	// ==================== PRESERVED EXISTING ENDPOINTS ====================

	@PostMapping("placeOrder/{customerId}")
	public void placeOrder(@PathVariable Long customerId) {
		orderService.placeOrder(customerId);
	}
	
	@PutMapping("updateOrderStatus/{orderId}/{status}")
	public String updateOrderStatus(
	        @PathVariable Long orderId,
	        @PathVariable String status,
	        Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			boolean isRestaurant = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_RESTAURANT"));
			String userEmail = authentication.getName();
			String result = orderService.updateOrderStatusByRole(orderId, status, userEmail, isAdmin, isRestaurant);
			return result;
		}
	    String result = orderService.updateOrderStatus(orderId, status);
	    return result;
	}
	
	@PostMapping("addOrder")
	public void addOrder(@RequestBody Order order) {
		orderService.addOrder(order);
	}

	@GetMapping("getAllOrders")
	public List<Order> getAllOrders() {
		List<Order> orders = orderService.getAllOrders();
		return orders;
	}

	@GetMapping("getOrder/{Id}")
	public Order getOrderById(@PathVariable Long Id, Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			if (!isAdmin) {
				orderService.verifyOrderAccess(Id, authentication.getName());
			}
		}
		Order order = orderService.getOrderById(Id);
		return order;
	}

	@PutMapping("updateOrder/{Id}")
	public String updateOrder(@RequestBody Order order, @PathVariable Long Id, Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			if (!isAdmin) {
				orderService.verifyOrderAccess(Id, authentication.getName());
			}
		}
		String result = orderService.updateOrder(order, Id);
		return result;
	}

	@DeleteMapping("deleteOrder/{Id}")
	public void deleteOrder(@PathVariable Long Id) {
		orderService.deleteOrder(Id);
	}
}