package com.foodify.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.model.Order;
import com.foodify.model.PaymentDetails;
import com.foodify.repository.OrderRepository;
import com.foodify.repository.PaymentDetailsRepository;

@Service
public class PaymentDetailsService {

	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;

	@Autowired
	private OrderRepository orderRepository;

	public String processPayment(Long orderId,String paymentMethod){

		Order order=orderRepository.findById(orderId)
		.orElseThrow(()-> new RuntimeException("Order not found"));
		order.getTotalAmount();
		
		PaymentDetails paymentDetails=new PaymentDetails();
		
		paymentDetails.setAmount(order.getTotalAmount());
		paymentDetails.setPaymentDate(LocalDateTime.now());
		paymentDetails.setPaymentMethod(paymentMethod);
		paymentDetails.setPaymentStatus("pending");
		
		order.setPaymentDetails(paymentDetails);
		
		paymentDetailsRepository.save(paymentDetails);
		orderRepository.save(order);
		
		return "Payment initiated successfully";
	}
	
	public String updatePaymentStatus(Long paymentId, String status) {

	    PaymentDetails payment = paymentDetailsRepository.findById(paymentId)
	            .orElseThrow(() -> new RuntimeException("Payment not found"));

	    List<String> validStatuses = List.of("PENDING", "SUCCESS", "FAILED");

	    String newStatus = status.toUpperCase();

	    if (!validStatuses.contains(newStatus)) {
	        throw new RuntimeException("Invalid payment status");
	    }

	    payment.setPaymentStatus(newStatus);

	    paymentDetailsRepository.save(payment);

	    return "Payment status updated successfully";
	}
	
	public void addPaymentDetails(PaymentDetails paymentDetails) {
		paymentDetailsRepository.save(paymentDetails);
	}

	public List<PaymentDetails> getAllPaymentDetails() {
		return paymentDetailsRepository.findAll();
	}

	public PaymentDetails getPaymentDetailsById(Long id) {
		return paymentDetailsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("PaymentDetails not found with id: " + id));
	}

	public String updatePaymentDetails(PaymentDetails paymentDetails, Long id) {
		if (paymentDetailsRepository.existsById(id)) {
			paymentDetailsRepository.save(paymentDetails);
			return "PaymentDetails updated successfully";
		}
		return "PaymentDetails not found";
	}

	public void deletePaymentDetails(Long id) {
		paymentDetailsRepository.deleteById(id);
	}
}