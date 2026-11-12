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

import com.foodify.model.Order;
import com.foodify.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("placeOrder/{customerId}")
	public void placeOrder(@PathVariable Long customerId) {
		orderService.placeOrder(customerId);
	}
	
	@PutMapping("updateOrderStatus/{orderId}/{status}")
	public String updateOrderStatus(
	        @PathVariable Long orderId,
	        @PathVariable String status) {
	    return orderService.updateOrderStatus(orderId, status);
	}
	
	@PostMapping("addOrder")
	public void addOrder(@RequestBody Order order) {
		orderService.addOrder(order);
	}

	@GetMapping("getAllOrders")
	public List<Order> getAllOrders() {
		return orderService.getAllOrders();
	}

	@GetMapping("getOrder/{Id}")
	public Order getOrderById(@PathVariable Long Id) {
		return orderService.getOrderById(Id);
	}

	@PutMapping("updateOrder/{Id}")
	public String updateOrder(@RequestBody Order order, @PathVariable Long Id) {
		return orderService.updateOrder(order, Id);
	}

	@DeleteMapping("deleteOrder/{Id}")
	public void deleteOrder(@PathVariable Long Id) {
		orderService.deleteOrder(Id);
	}
}