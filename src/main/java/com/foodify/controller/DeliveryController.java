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

import com.foodify.model.Delivery;
import com.foodify.service.DeliveryService;

@RestController
public class DeliveryController {

	@Autowired
	private DeliveryService deliveryService;

	
	@PostMapping("assignDelivery/{orderId}")
	public String assignDelivery(
	        @PathVariable Long orderId,
	        @RequestBody String deliveryAddress) {

	    return deliveryService.assignDelivery(orderId, deliveryAddress);
	}
	
	@PutMapping("updateDeliveryStatus/{deliveryId}/{status}")
	public String updateDeliveryStatus(
	        @PathVariable Long deliveryId,
	        @PathVariable String status) {

	    return deliveryService.updateDeliveryStatus(deliveryId, status);
	}
	
	@PostMapping("addDelivery")
	public void addDeliveryDetails(@RequestBody Delivery delivery) {
		deliveryService.addDeliveryDetails(delivery);
	}

	@GetMapping("getAllDeliveries")
	public List<Delivery> getAllDelivery() {
		return deliveryService.getAllDelivery();
	}

	@GetMapping("getDelivery/{Id}")
	public Delivery getDelivery(@PathVariable Long Id) {
		return deliveryService.getDelivery(Id);
	}

	@PutMapping("updateDelivery/{Id}")
	public String updateDelivery(@RequestBody Delivery delivery, @PathVariable Long Id) {
		return deliveryService.updateDelivery(delivery, Id);
	}

	@DeleteMapping("deleteDelivery/{Id}")
	public void deleteDelivery(@PathVariable Long Id) {
		deliveryService.deleteDelivery(Id);
	}
}