package com.foodify.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.model.Delivery;
import com.foodify.model.Order;
import com.foodify.repository.DeliveryRepository;
import com.foodify.repository.OrderRepository;

@Service
public class DeliveryService {

	@Autowired
	private DeliveryRepository deliveryReposotory;


	@Autowired
	private OrderRepository orderRepository;


	public String assignDelivery(Long orderId, String deliveryAddress) {

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    if (order.getDelivery() != null) {
	        throw new RuntimeException("Delivery already assigned");
	    }

	    Delivery delivery = new Delivery();

	    delivery.setDeliveryAddress(deliveryAddress);
	    delivery.setDeliveryStatus("ASSIGNED");
	    delivery.setAssignedAt(LocalDateTime.now());
	    delivery.setTrackingNumber(UUID.randomUUID().toString());

	    order.setDelivery(delivery);

	    deliveryReposotory.save(delivery);
	    orderRepository.save(order);

	    return "Delivery assigned successfully";
	}
	public String updateDeliveryStatus(Long deliveryId, String status) {

	    Delivery delivery = deliveryReposotory.findById(deliveryId)
	            .orElseThrow(() -> new RuntimeException("Delivery not found"));

	    if (status == null) {
	        throw new RuntimeException("Delivery status cannot be null");
	    }

	    String newStatus = status.toUpperCase();

	    List<String> validStatuses = List.of(
	            "ASSIGNED",
	            "PICKED_UP",
	            "OUT_FOR_DELIVERY",
	            "DELIVERED"
	    );

	    if (!validStatuses.contains(newStatus)) {
	        throw new RuntimeException("Invalid delivery status");
	    }

	    String currentStatus = delivery.getDeliveryStatus();

	    if ("DELIVERED".equals(currentStatus)) {
	        throw new RuntimeException("Delivery already completed");
	    }

	    Map<String, String> nextStatus = Map.of(
	            "ASSIGNED", "PICKED_UP",
	            "PICKED_UP", "OUT_FOR_DELIVERY",
	            "OUT_FOR_DELIVERY", "DELIVERED"
	    );

	    if (!newStatus.equals(nextStatus.get(currentStatus))) {
	        throw new RuntimeException("Invalid delivery status transition");
	    }

	    delivery.setDeliveryStatus(newStatus);

	    if (newStatus.equals("DELIVERED")) {
	        delivery.setDeliveredAt(LocalDateTime.now());
	    }

	    deliveryReposotory.save(delivery);

	    return "Delivery status updated successfully";
	}	
	public void addDeliveryDetails(Delivery delivery ) {
		deliveryReposotory.save(delivery);
	}

	public List<Delivery> getAllDelivery() {
		List<Delivery> delivery=deliveryReposotory.findAll();
		return delivery;
	}

	public Delivery getDelivery(Long Id) {
		return deliveryReposotory.findById(Id).
				orElseThrow( ()-> new RuntimeException("Delivery is not found "+ Id));
	}

	public String updateDelivery(Delivery delivery,Long Id) {
		if(deliveryReposotory.existsById(Id)) {
			deliveryReposotory.save(delivery);
			return "Delivery found";
		}
		else {
			return "Delivery not found";
		}

	}
	public void deleteDelivery(Long Id) {
		deliveryReposotory.deleteById(Id);
	}
}
