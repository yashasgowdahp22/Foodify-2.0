package com.foodify.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.dto.AssignDeliveryRequestDTO;
import com.foodify.dto.DeliveryResponseDTO;
import com.foodify.exception.BadRequestException;
import com.foodify.exception.ResourceNotFoundException;
import com.foodify.exception.UnauthorizedException;
import com.foodify.model.Customer;
import com.foodify.model.Delivery;
import com.foodify.model.Order;
import com.foodify.model.PaymentDetails;
import com.foodify.repository.CustomerRepository;
import com.foodify.repository.DeliveryRepository;
import com.foodify.repository.OrderRepository;
import com.foodify.repository.PaymentDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class DeliveryService {

	@Autowired
	private DeliveryRepository deliveryRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;

	// ==================== PHASE 4 DELIVERY MANAGEMENT SERVICES ====================

	@Transactional
	public DeliveryResponseDTO assignDeliveryPartner(Long orderId, AssignDeliveryRequestDTO request) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		if (order.getDelivery() != null) {
			throw new BadRequestException("Delivery is already assigned to this order");
		}

		String orderStatus = order.getStatus().toUpperCase();
		if (!orderStatus.equals("PREPARING")) {
			throw new BadRequestException("Delivery partners can only be assigned to orders with status 'PREPARING'. Current status is: " + orderStatus);
		}

		Customer deliveryPartner = null;
		if (request != null && request.getDeliveryPartnerEmail() != null && !request.getDeliveryPartnerEmail().trim().isEmpty()) {
			deliveryPartner = customerRepository.findByEmail(request.getDeliveryPartnerEmail().trim())
					.orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found with email: " + request.getDeliveryPartnerEmail()));
		} else if (request != null && request.getDeliveryPartnerId() != null) {
			deliveryPartner = customerRepository.findById(request.getDeliveryPartnerId())
					.orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found with id: " + request.getDeliveryPartnerId()));
		} else {
			throw new BadRequestException("Delivery partner ID or email must be provided");
		}

		if (!"DELIVERY".equalsIgnoreCase(deliveryPartner.getRole())) {
			throw new BadRequestException("Assigned user must have role 'DELIVERY'. Provided user has role: " + deliveryPartner.getRole());
		}

		String deliveryAddress = (request != null && request.getDeliveryAddress() != null && !request.getDeliveryAddress().trim().isEmpty())
				? request.getDeliveryAddress().trim()
				: order.getDeliveryAddress();

		if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
			deliveryAddress = order.getCustomer() != null ? order.getCustomer().getAddress() : "Customer Address";
		}

		Delivery delivery = new Delivery();
		delivery.setDeliveryAddress(deliveryAddress);
		delivery.setDeliveryStatus("ASSIGNED");
		delivery.setAssignedAt(LocalDateTime.now());
		delivery.setTrackingNumber("TRK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
		delivery.setDeliveryPartner(deliveryPartner);
		delivery.setOrder(order);

		order.setDelivery(delivery);

		Delivery savedDelivery = deliveryRepository.save(delivery);
		orderRepository.save(order);

		DeliveryResponseDTO response = mapToDeliveryResponseDTO(savedDelivery);
		return response;
	}

	public List<DeliveryResponseDTO> getMyDeliveries(String partnerEmail) {
		List<Delivery> deliveries = deliveryRepository.findByDeliveryPartnerEmailOrderByAssignedAtDesc(partnerEmail);
		List<DeliveryResponseDTO> responseList = new ArrayList<>();
		for (Delivery delivery : deliveries) {
			DeliveryResponseDTO dto = mapToDeliveryResponseDTO(delivery);
			responseList.add(dto);
		}
		return responseList;
	}

	public DeliveryResponseDTO getMyDeliveryById(String partnerEmail, Long deliveryId) {
		Delivery delivery = deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

		if (delivery.getDeliveryPartner() == null || !delivery.getDeliveryPartner().getEmail().equalsIgnoreCase(partnerEmail)) {
			throw new UnauthorizedException("You are not authorized to view this delivery");
		}

		DeliveryResponseDTO response = mapToDeliveryResponseDTO(delivery);
		return response;
	}

	@Transactional
	public DeliveryResponseDTO updateDeliveryStatusByPartner(String partnerEmail, Long deliveryId, String status, boolean isAdmin) {
		Delivery delivery = deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

		if (!isAdmin) {
			if (delivery.getDeliveryPartner() == null || !delivery.getDeliveryPartner().getEmail().equalsIgnoreCase(partnerEmail)) {
				throw new UnauthorizedException("You are not authorized to update this delivery");
			}
		}

		if (status == null || status.trim().isEmpty()) {
			throw new BadRequestException("Delivery status cannot be null or empty");
		}

		String currentStatus = delivery.getDeliveryStatus().toUpperCase();
		String newStatus = status.trim().toUpperCase();

		List<String> validStatuses = List.of("ASSIGNED", "PICKED_UP", "OUT_FOR_DELIVERY", "DELIVERED");
		if (!validStatuses.contains(newStatus)) {
			throw new BadRequestException("Invalid delivery status: " + newStatus);
		}

		if ("DELIVERED".equals(currentStatus)) {
			throw new BadRequestException("Delivery already completed");
		}

		Map<String, String> nextStatus = Map.of(
				"ASSIGNED", "PICKED_UP",
				"PICKED_UP", "OUT_FOR_DELIVERY",
				"OUT_FOR_DELIVERY", "DELIVERED"
		);

		if (!newStatus.equals(nextStatus.get(currentStatus))) {
			throw new BadRequestException("Invalid delivery status transition from " + currentStatus + " to " + newStatus);
		}

		delivery.setDeliveryStatus(newStatus);

		Order order = delivery.getOrder();
		if (newStatus.equals("OUT_FOR_DELIVERY")) {
			if (order != null) {
				order.setStatus("OUT_FOR_DELIVERY");
				orderRepository.save(order);
			}
		} else if (newStatus.equals("DELIVERED")) {
			delivery.setDeliveredAt(LocalDateTime.now());
			if (order != null) {
				order.setStatus("DELIVERED");
				PaymentDetails payment = order.getPaymentDetails();
				if (payment != null && "COD".equalsIgnoreCase(payment.getPaymentMethod()) && "PENDING".equalsIgnoreCase(payment.getPaymentStatus())) {
					payment.setPaymentStatus("SUCCESS");
					String txnId = "TXN-COD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
					payment.setTransactionId(txnId);
					payment.setPaymentDate(LocalDateTime.now());
					paymentDetailsRepository.save(payment);
				}
				orderRepository.save(order);
			}
		}

		Delivery savedDelivery = deliveryRepository.save(delivery);
		DeliveryResponseDTO response = mapToDeliveryResponseDTO(savedDelivery);
		return response;
	}

	public List<Customer> getAvailableDeliveryPartners() {
		List<Customer> partners = customerRepository.findByRole("DELIVERY");
		return partners;
	}

	public DeliveryResponseDTO mapToDeliveryResponseDTO(Delivery delivery) {
		DeliveryResponseDTO dto = new DeliveryResponseDTO();
		dto.setDeliveryId(delivery.getId());
		dto.setDeliveryStatus(delivery.getDeliveryStatus());
		dto.setTrackingNumber(delivery.getTrackingNumber());
		dto.setAssignedAt(delivery.getAssignedAt());
		dto.setDeliveredAt(delivery.getDeliveredAt());
		dto.setDeliveryAddress(delivery.getDeliveryAddress());

		Order order = delivery.getOrder();
		if (order != null) {
			dto.setOrderId(order.getId());
			dto.setOrderStatus(order.getStatus());
			dto.setTotalAmount(order.getTotalAmount());

			if (order.getPaymentDetails() != null) {
				dto.setPaymentMethod(order.getPaymentDetails().getPaymentMethod());
				dto.setPaymentStatus(order.getPaymentDetails().getPaymentStatus());
			}

			if (order.getRestaurant() != null) {
				dto.setRestaurantId(order.getRestaurant().getRestaurantId());
				dto.setRestaurantName(order.getRestaurant().getRestaurantName());
				dto.setRestaurantAddress(order.getRestaurant().getAddress());
			}

			if (order.getCustomer() != null) {
				dto.setCustomerId(order.getCustomer().getId());
				dto.setCustomerName(order.getCustomer().getName());
				dto.setCustomerAddress(order.getCustomer().getAddress());
			}
		}

		Customer partner = delivery.getDeliveryPartner();
		if (partner != null) {
			dto.setDeliveryPartnerId(partner.getId());
			dto.setDeliveryPartnerName(partner.getName());
			dto.setDeliveryPartnerEmail(partner.getEmail());
		}

		return dto;
	}

	// ==================== PRESERVED EXISTING METHODS ====================

	@Transactional
	public String assignDelivery(Long orderId, String deliveryAddress) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		if (order.getDelivery() != null) {
			throw new BadRequestException("Delivery already assigned to this order");
		}

		String orderStatus = order.getStatus().toUpperCase();
		if (!orderStatus.equals("PREPARING")) {
			throw new BadRequestException("Delivery can only be assigned to orders with status 'PREPARING'. Current status is: " + orderStatus);
		}

		Delivery delivery = new Delivery();
		delivery.setDeliveryAddress(deliveryAddress);
		delivery.setDeliveryStatus("ASSIGNED");
		delivery.setAssignedAt(LocalDateTime.now());
		delivery.setTrackingNumber("TRK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());

		order.setDelivery(delivery);

		deliveryRepository.save(delivery);
		orderRepository.save(order);

		return "Delivery assigned successfully";
	}

	@Transactional
	public String updateDeliveryStatus(Long deliveryId, String status) {
		Delivery delivery = deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

		if (status == null || status.trim().isEmpty()) {
			throw new BadRequestException("Delivery status cannot be null or empty");
		}

		String newStatus = status.trim().toUpperCase();
		List<String> validStatuses = List.of("ASSIGNED", "PICKED_UP", "OUT_FOR_DELIVERY", "DELIVERED");
		if (!validStatuses.contains(newStatus)) {
			throw new BadRequestException("Invalid delivery status: " + newStatus);
		}

		String currentStatus = delivery.getDeliveryStatus();
		if ("DELIVERED".equals(currentStatus)) {
			throw new BadRequestException("Delivery already completed");
		}

		Map<String, String> nextStatus = Map.of(
				"ASSIGNED", "PICKED_UP",
				"PICKED_UP", "OUT_FOR_DELIVERY",
				"OUT_FOR_DELIVERY", "DELIVERED"
		);

		if (!newStatus.equals(nextStatus.get(currentStatus))) {
			throw new BadRequestException("Invalid delivery status transition from " + currentStatus + " to " + newStatus);
		}

		delivery.setDeliveryStatus(newStatus);

		Order order = delivery.getOrder();
		if (newStatus.equals("OUT_FOR_DELIVERY")) {
			if (order != null) {
				order.setStatus("OUT_FOR_DELIVERY");
				orderRepository.save(order);
			}
		} else if (newStatus.equals("DELIVERED")) {
			delivery.setDeliveredAt(LocalDateTime.now());
			if (order != null) {
				order.setStatus("DELIVERED");
				PaymentDetails payment = order.getPaymentDetails();
				if (payment != null && "COD".equalsIgnoreCase(payment.getPaymentMethod()) && "PENDING".equalsIgnoreCase(payment.getPaymentStatus())) {
					payment.setPaymentStatus("SUCCESS");
					String txnId = "TXN-COD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
					payment.setTransactionId(txnId);
					payment.setPaymentDate(LocalDateTime.now());
					paymentDetailsRepository.save(payment);
				}
				orderRepository.save(order);
			}
		}

		deliveryRepository.save(delivery);
		return "Delivery status updated successfully";
	}

	public void addDeliveryDetails(Delivery delivery) {
		deliveryRepository.save(delivery);
	}

	public List<Delivery> getAllDelivery() {
		List<Delivery> deliveryList = deliveryRepository.findAll();
		return deliveryList;
	}

	public Delivery getDelivery(Long Id) {
		Delivery delivery = deliveryRepository.findById(Id)
				.orElseThrow(() -> new ResourceNotFoundException("Delivery is not found with id: " + Id));
		return delivery;
	}

	public String updateDelivery(Delivery delivery, Long Id) {
		if (deliveryRepository.existsById(Id)) {
			deliveryRepository.save(delivery);
			return "Delivery found";
		} else {
			return "Delivery not found";
		}
	}

	public void deleteDelivery(Long Id) {
		deliveryRepository.deleteById(Id);
	}
}
