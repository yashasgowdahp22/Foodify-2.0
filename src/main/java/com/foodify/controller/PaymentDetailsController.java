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

import com.foodify.model.PaymentDetails;
import com.foodify.service.PaymentDetailsService;

@RestController
public class PaymentDetailsController {

	@Autowired
	private PaymentDetailsService paymentDetailsService;

	@PostMapping("processPayment/{orderId}/{paymentMethod}")
	public String processPayment(
			@PathVariable Long orderId,
			@PathVariable String paymentMethod) {
		String processPayment = paymentDetailsService.processPayment(orderId, paymentMethod);
		return processPayment;
	}
	
	@PutMapping("updatePaymentStatus/{paymentId}/{status}")
	public String updatePaymentStatus(
	        @PathVariable Long paymentId,
	        @PathVariable String status) {

	    return paymentDetailsService.updatePaymentStatus(paymentId, status);
	}
	
	
	@PostMapping("addPaymentDetails")
	public void addPaymentDetails(@RequestBody PaymentDetails paymentDetails) {
		paymentDetailsService.addPaymentDetails(paymentDetails);
	}

	@GetMapping("getAllPaymentDetails")
	public List<PaymentDetails> getAllPaymentDetails() {
		return paymentDetailsService.getAllPaymentDetails();
	}

	@GetMapping("getPaymentDetails/{Id}")
	public PaymentDetails getPaymentDetailsById(@PathVariable Long Id) {
		return paymentDetailsService.getPaymentDetailsById(Id);
	}

	@PutMapping("updatePaymentDetails/{Id}")
	public String updatePaymentDetails(@RequestBody PaymentDetails paymentDetails, @PathVariable Long Id) {
		return paymentDetailsService.updatePaymentDetails(paymentDetails, Id);
	}

	@DeleteMapping("deletePaymentDetails/{Id}")
	public void deletePaymentDetails(@PathVariable Long Id) {
		paymentDetailsService.deletePaymentDetails(Id);
	}
}