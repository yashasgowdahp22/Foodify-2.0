package com.foodify.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.model.OrderItem;
import com.foodify.repository.MenuItemRepository;
import com.foodify.repository.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;
	
	public void addOrderItem(OrderItem orderItem) {
		orderItemRepository.save(orderItem);
	}
	
	public List<OrderItem> getAllOrderItem(){
		List<OrderItem> orderItem = orderItemRepository.findAll();
		return orderItem;
	}
	
	public OrderItem getOrderItem(Long Id) {
		return orderItemRepository.findById(Id).
				orElseThrow(()-> new RuntimeException("OrderItem not found " + Id));
	}
	
	public String updateOrderItem(OrderItem orderItem,Long Id) {
		if(orderItemRepository.existsById(Id)) {
			orderItemRepository.save(orderItem);
			return "OrderItem found";
		}
		else {
			return "OrderItem not found";
		}
	}
	
	public void deleteOrderItem(Long Id) {
		orderItemRepository.deleteById(Id);
	}
	
}
