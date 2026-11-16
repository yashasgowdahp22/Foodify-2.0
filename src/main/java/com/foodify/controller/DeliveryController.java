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

import com.foodify.dto.AssignDeliveryRequestDTO;
import com.foodify.dto.DeliveryResponseDTO;
import com.foodify.model.Customer;
import com.foodify.model.Delivery;
import com.foodify.service.DeliveryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Delivery Management", description = "Delivery APIs for Admin and Delivery Partners")
@RestController
public class DeliveryController {

	@Autowired
	private DeliveryService deliveryService;

	// ==================== NEW PHASE 4 ENDPOINTS ====================

	@Operation(summary = "Assign delivery partner to an order (Admin only)")
	@PostMapping("assignDelivery/{orderId}")
	public ResponseEntity<DeliveryResponseDTO> assignDelivery(
	        @PathVariable Long orderId,
	        @RequestBody(required = false) AssignDeliveryRequestDTO request) {
		DeliveryResponseDTO response = deliveryService.assignDeliveryPartner(orderId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Get all deliveries assigned to currently logged-in delivery partner")
	@GetMapping("myDeliveries")
	public ResponseEntity<List<DeliveryResponseDTO>> getMyDeliveries(Authentication authentication) {
		String partnerEmail = authentication.getName();
		List<DeliveryResponseDTO> deliveries = deliveryService.getMyDeliveries(partnerEmail);
		return ResponseEntity.ok(deliveries);
	}

	@Operation(summary = "Get delivery details for currently logged-in delivery partner")
	@GetMapping("myDeliveries/{deliveryId}")
	public ResponseEntity<DeliveryResponseDTO> getMyDeliveryById(
			Authentication authentication,
			@PathVariable Long deliveryId) {
		String partnerEmail = authentication.getName();
		DeliveryResponseDTO delivery = deliveryService.getMyDeliveryById(partnerEmail, deliveryId);
		return ResponseEntity.ok(delivery);
	}

	@Operation(summary = "Update delivery status for assigned delivery partner")
	@PutMapping("myDeliveries/{deliveryId}/status/{status}")
	public ResponseEntity<DeliveryResponseDTO> updateMyDeliveryStatus(
			Authentication authentication,
			@PathVariable Long deliveryId,
			@PathVariable String status) {
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		String partnerEmail = authentication.getName();
		DeliveryResponseDTO response = deliveryService.updateDeliveryStatusByPartner(partnerEmail, deliveryId, status, isAdmin);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "List all registered delivery partners (Admin only)")
	@GetMapping("availableDeliveryPartners")
	public ResponseEntity<List<Customer>> getAvailableDeliveryPartners() {
		List<Customer> partners = deliveryService.getAvailableDeliveryPartners();
		return ResponseEntity.ok(partners);
	}

	// ==================== PRESERVED EXISTING ENDPOINTS ====================

	@Operation(summary = "Update delivery status (Legacy / Admin / Delivery Partner)")
	@PutMapping("updateDeliveryStatus/{deliveryId}/{status}")
	public String updateDeliveryStatus(
	        @PathVariable Long deliveryId,
	        @PathVariable String status,
	        Authentication authentication) {
		if (authentication != null) {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
			String partnerEmail = authentication.getName();
			deliveryService.updateDeliveryStatusByPartner(partnerEmail, deliveryId, status, isAdmin);
			return "Delivery status updated successfully";
		}
	    String result = deliveryService.updateDeliveryStatus(deliveryId, status);
	    return result;
	}
	
	@PostMapping("addDelivery")
	public void addDeliveryDetails(@RequestBody Delivery delivery) {
		deliveryService.addDeliveryDetails(delivery);
	}

	@GetMapping("getAllDeliveries")
	public List<Delivery> getAllDelivery() {
		List<Delivery> deliveries = deliveryService.getAllDelivery();
		return deliveries;
	}

	@GetMapping("getDelivery/{Id}")
	public Delivery getDelivery(@PathVariable Long Id) {
		Delivery delivery = deliveryService.getDelivery(Id);
		return delivery;
	}

	@PutMapping("updateDelivery/{Id}")
	public String updateDelivery(@RequestBody Delivery delivery, @PathVariable Long Id) {
		String result = deliveryService.updateDelivery(delivery, Id);
		return result;
	}

	@DeleteMapping("deleteDelivery/{Id}")
	public void deleteDelivery(@PathVariable Long Id) {
		deliveryService.deleteDelivery(Id);
	}
}