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

import com.foodify.model.OrderItem;
import com.foodify.service.OrderItemService;

@RestController
public class OrderItemController {

	@Autowired
	private OrderItemService orderItemService;

	@PostMapping("addOrderItem")
	public void addOrderItem(@RequestBody OrderItem orderItem) {
		orderItemService.addOrderItem(orderItem);
	}

	@GetMapping("getAllOrderItems")
	public List<OrderItem> getAllOrderItem() {
		return orderItemService.getAllOrderItem();
	}

	@GetMapping("getOrderItem/{Id}")
	public OrderItem getOrderItem(@PathVariable Long Id) {
		return orderItemService.getOrderItem(Id);
	}

	@PutMapping("updateOrderItem/{Id}")
	public String updateOrderItem(@RequestBody OrderItem orderItem, @PathVariable Long Id) {
		return orderItemService.updateOrderItem(orderItem, Id);
	}

	@DeleteMapping("deleteOrderItem/{Id}")
	public void deleteOrderItem(@PathVariable Long Id) {
		orderItemService.deleteOrderItem(Id);
	}
}