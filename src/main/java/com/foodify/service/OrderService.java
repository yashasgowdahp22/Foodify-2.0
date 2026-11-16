package com.foodify.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.dto.CheckoutRequestDTO;
import com.foodify.dto.OrderItemResponseDTO;
import com.foodify.dto.OrderResponseDTO;
import com.foodify.exception.BadRequestException;
import com.foodify.exception.ResourceNotFoundException;
import com.foodify.exception.UnauthorizedException;
import com.foodify.model.Cart;
import com.foodify.model.CartItem;
import com.foodify.model.Customer;
import com.foodify.model.MenuItem;
import com.foodify.model.Order;
import com.foodify.model.OrderItem;
import com.foodify.model.PaymentDetails;
import com.foodify.model.Restaurant;
import com.foodify.repository.CartRepository;
import com.foodify.repository.CustomerRepository;
import com.foodify.repository.OrderRepository;
import com.foodify.repository.PaymentDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;

	// ==================== NEW PHASE 3 ORDER & CHECKOUT SERVICES ====================

	@Transactional
	public OrderResponseDTO checkout(String customerEmail, CheckoutRequestDTO request) {
		Customer customer = customerRepository.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + customerEmail));

		Cart cart = customer.getCart();
		if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
			throw new BadRequestException("Your cart is empty. Please add items before checkout.");
		}

		// Revalidate all items and restaurant
		for (CartItem cartItem : cart.getCartItems()) {
			MenuItem menuItem = cartItem.getMenuItem();
			if (menuItem == null || !menuItem.isAvailable()) {
				String itemName = menuItem != null ? menuItem.getName() : "Item";
				throw new BadRequestException("Menu item '" + itemName + "' is no longer available.");
			}
			if (menuItem.getRestaurant() != null && !menuItem.getRestaurant().isOpen()) {
				throw new BadRequestException("Restaurant '" + menuItem.getRestaurant().getRestaurantName() + "' is currently closed.");
			}
		}

		// Resolve delivery address
		String deliveryAddress = request != null ? request.getDeliveryAddress() : null;
		if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
			deliveryAddress = customer.getAddress();
		}
		if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
			deliveryAddress = "Default Delivery Address";
		}

		// Validate payment method
		String paymentMethod = (request != null && request.getPaymentMethod() != null) ? request.getPaymentMethod() : "COD";
		String methodUpper = paymentMethod.trim().toUpperCase();
		if (!List.of("COD", "UPI", "CARD").contains(methodUpper)) {
			throw new BadRequestException("Invalid payment method: " + paymentMethod + ". Allowed methods: COD, UPI, CARD");
		}

		Order order = new Order();
		order.setCustomer(customer);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus("PENDING");
		order.setDeliveryAddress(deliveryAddress);
		order.setSpecialInstructions(request != null ? request.getSpecialInstructions() : null);

		CartItem firstCartItem = cart.getCartItems().get(0);
		Restaurant restaurant = firstCartItem.getMenuItem().getRestaurant();
		order.setRestaurant(restaurant);

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (CartItem cartItem : cart.getCartItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setMenuItem(cartItem.getMenuItem());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getPrice());

			BigDecimal subTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			orderItem.setSubTotal(subTotal);

			order.addOrderItem(orderItem);
			totalAmount = totalAmount.add(subTotal);
		}
		order.setTotalAmount(totalAmount);

		PaymentDetails payment = new PaymentDetails();
		payment.setAmount(totalAmount);
		payment.setPaymentMethod(methodUpper);
		payment.setPaymentDate(LocalDateTime.now());

		if (methodUpper.equals("COD")) {
			payment.setPaymentStatus("PENDING");
			payment.setTransactionId(null);
		} else {
			payment.setPaymentStatus("SUCCESS");
			String txnId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
			payment.setTransactionId(txnId);
		}

		PaymentDetails savedPayment = paymentDetailsRepository.save(payment);
		order.setPaymentDetails(savedPayment);

		Order savedOrder = orderRepository.save(order);

		// Clear customer cart after successful order creation
		cart.getCartItems().clear();
		cart.setUpdatedAt(LocalDateTime.now());
		cartRepository.save(cart);

		OrderResponseDTO response = mapToOrderResponseDTO(savedOrder);
		return response;
	}

	public List<OrderResponseDTO> getMyOrders(String customerEmail) {
		List<Order> orders = orderRepository.findByCustomerEmailOrderByOrderDateDesc(customerEmail);
		List<OrderResponseDTO> responseList = new ArrayList<>();
		for (Order order : orders) {
			OrderResponseDTO dto = mapToOrderResponseDTO(order);
			responseList.add(dto);
		}
		return responseList;
	}

	public OrderResponseDTO getMyOrderById(String customerEmail, Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		if (order.getCustomer() == null || !order.getCustomer().getEmail().equalsIgnoreCase(customerEmail)) {
			throw new UnauthorizedException("You are not authorized to view this order");
		}

		OrderResponseDTO response = mapToOrderResponseDTO(order);
		return response;
	}

	@Transactional
	public OrderResponseDTO cancelMyOrder(String customerEmail, Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		if (order.getCustomer() == null || !order.getCustomer().getEmail().equalsIgnoreCase(customerEmail)) {
			throw new UnauthorizedException("You are not authorized to cancel this order");
		}

		String currentStatus = order.getStatus().toUpperCase();
		if (!currentStatus.equals("PENDING")) {
			throw new BadRequestException("Only PENDING orders can be cancelled. Current status is: " + currentStatus);
		}

		order.setStatus("CANCELLED");

		PaymentDetails payment = order.getPaymentDetails();
		if (payment != null && "SUCCESS".equalsIgnoreCase(payment.getPaymentStatus())) {
			payment.setPaymentStatus("REFUNDED");
			paymentDetailsRepository.save(payment);
		}

		Order savedOrder = orderRepository.save(order);
		OrderResponseDTO response = mapToOrderResponseDTO(savedOrder);
		return response;
	}

	public List<OrderResponseDTO> getRestaurantOrders(String ownerEmail) {
		List<Order> orders = orderRepository.findByRestaurantOwnerEmailOrderByOrderDateDesc(ownerEmail);
		List<OrderResponseDTO> responseList = new ArrayList<>();
		for (Order order : orders) {
			OrderResponseDTO dto = mapToOrderResponseDTO(order);
			responseList.add(dto);
		}
		return responseList;
	}

	@Transactional
	public String updateOrderStatusByRole(Long orderId, String newStatus, String userEmail, boolean isAdmin, boolean isRestaurant) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		String currentStatus = order.getStatus().toUpperCase();
		String requestedStatus = newStatus.toUpperCase();

		if (isRestaurant && !isAdmin) {
			if (order.getRestaurant() == null || 
				order.getRestaurant().getOwner() == null || 
				!order.getRestaurant().getOwner().getEmail().equalsIgnoreCase(userEmail)) {
				throw new UnauthorizedException("You are not authorized to update orders for this restaurant");
			}
			if (requestedStatus.equals("OUT_FOR_DELIVERY") || requestedStatus.equals("DELIVERED")) {
				throw new BadRequestException("Restaurant owners cannot mark orders as " + requestedStatus + ". Dispatch and delivery actions are reserved for delivery partners.");
			}
		}

		if (!isAdmin && !isRestaurant) {
			if (order.getCustomer() == null || !order.getCustomer().getEmail().equalsIgnoreCase(userEmail)) {
				throw new UnauthorizedException("You are not authorized to update this order");
			}
			if (!requestedStatus.equals("CANCELLED")) {
				throw new BadRequestException("Customers can only cancel orders, not change status to " + requestedStatus);
			}
			if (!currentStatus.equals("PENDING")) {
				throw new BadRequestException("Customers can only cancel PENDING orders");
			}
		}

		if (currentStatus.equals("DELIVERED") || currentStatus.equals("CANCELLED")) {
			throw new BadRequestException("Order already completed");
		}

		if (!List.of("PENDING", "CONFIRMED", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")
				.contains(requestedStatus)) {
			throw new BadRequestException("Invalid order status: " + requestedStatus);
		}

		Map<String, String> nextStatus = Map.of(
				"PENDING", "CONFIRMED",
				"CONFIRMED", "PREPARING",
				"PREPARING", "OUT_FOR_DELIVERY",
				"OUT_FOR_DELIVERY", "DELIVERED"
		);

		if (!requestedStatus.equals("CANCELLED") && !requestedStatus.equals(nextStatus.get(currentStatus))) {
			throw new BadRequestException("Invalid order status transition from " + currentStatus + " to " + requestedStatus);
		}

		order.setStatus(requestedStatus);

		if (requestedStatus.equals("CANCELLED")) {
			PaymentDetails payment = order.getPaymentDetails();
			if (payment != null && "SUCCESS".equalsIgnoreCase(payment.getPaymentStatus())) {
				payment.setPaymentStatus("REFUNDED");
				paymentDetailsRepository.save(payment);
			}
		}

		orderRepository.save(order);
		return "Status Changed Successfully";
	}

	public OrderResponseDTO mapToOrderResponseDTO(Order order) {
		OrderResponseDTO dto = new OrderResponseDTO();
		dto.setId(order.getId());
		dto.setOrderDate(order.getOrderDate());
		dto.setStatus(order.getStatus());
		dto.setTotalAmount(order.getTotalAmount());
		dto.setDeliveryAddress(order.getDeliveryAddress());
		dto.setSpecialInstructions(order.getSpecialInstructions());

		if (order.getCustomer() != null) {
			dto.setCustomerId(order.getCustomer().getId());
			dto.setCustomerName(order.getCustomer().getName());
			dto.setCustomerEmail(order.getCustomer().getEmail());
		}

		if (order.getRestaurant() != null) {
			dto.setRestaurantId(order.getRestaurant().getRestaurantId());
			dto.setRestaurantName(order.getRestaurant().getRestaurantName());
		}

		if (order.getPaymentDetails() != null) {
			dto.setPaymentMethod(order.getPaymentDetails().getPaymentMethod());
			dto.setPaymentStatus(order.getPaymentDetails().getPaymentStatus());
			dto.setTransactionId(order.getPaymentDetails().getTransactionId());
		}

		List<OrderItemResponseDTO> itemDTOList = new ArrayList<>();
		if (order.getOrderItems() != null) {
			for (OrderItem item : order.getOrderItems()) {
				OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
				itemDTO.setId(item.getId());
				if (item.getMenuItem() != null) {
					itemDTO.setMenuItemId(item.getMenuItem().getId());
					itemDTO.setMenuItemName(item.getMenuItem().getName());
				}
				itemDTO.setPrice(item.getPrice());
				itemDTO.setQuantity(item.getQuantity());
				itemDTO.setSubTotal(item.getSubTotal());
				itemDTOList.add(itemDTO);
			}
		}
		dto.setItems(itemDTOList);

		return dto;
	}

	public void verifyOrderAccess(Long orderId, String userEmail) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

		boolean isCustomer = order.getCustomer() != null && order.getCustomer().getEmail().equalsIgnoreCase(userEmail);
		boolean isRestaurant = order.getRestaurant() != null && order.getRestaurant().getOwner() != null && order.getRestaurant().getOwner().getEmail().equalsIgnoreCase(userEmail);
		boolean isDelivery = order.getDelivery() != null && order.getDelivery().getDeliveryPartner() != null && order.getDelivery().getDeliveryPartner().getEmail().equalsIgnoreCase(userEmail);

		if (!isCustomer && !isRestaurant && !isDelivery) {
			throw new UnauthorizedException("You are not authorized to access this order");
		}
	}

	// ==================== EXISTING METHODS PRESERVED ====================

	@Transactional
	public void placeOrder(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new RuntimeException("customer not found"));

		Cart cart = cartRepository.findByCustomerId(customerId)
				.orElseThrow(() -> new RuntimeException("cart not found"));

		if (cart.getCartItems().isEmpty()) {
			throw new RuntimeException("cart is  empty");
		}

		Order order = new Order();

		order.setCustomer(customer);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus("PENDING");
		order.setDeliveryAddress(customer.getAddress());

		CartItem firstCartItem = cart.getCartItems().get(0);
		if (firstCartItem.getMenuItem() != null && firstCartItem.getMenuItem().getRestaurant() != null) {
			order.setRestaurant(firstCartItem.getMenuItem().getRestaurant());
		}

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (CartItem cartItem : cart.getCartItems()) {
			OrderItem orderItem = new OrderItem();

			orderItem.setMenuItem(cartItem.getMenuItem());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getPrice());

			BigDecimal subTotal = cartItem.getPrice()
					.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			orderItem.setSubTotal(subTotal);

			order.addOrderItem(orderItem);
			totalAmount = totalAmount.add(subTotal); 
		}
		order.setTotalAmount(totalAmount);
		orderRepository.save(order);
		cart.getCartItems().clear();
	}

	@Transactional
	public String updateOrderStatus(Long orderId, String status) {

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException(
	                    "Order not found with id: " + orderId));

	    String currentStatus = order.getStatus().toUpperCase();
	    String newStatus = status.toUpperCase();

	    if (currentStatus.equals("DELIVERED") ||
	            currentStatus.equals("CANCELLED")) {
	        throw new RuntimeException("Order already completed");
	    }

	    if (!List.of("PENDING", "CONFIRMED", "PREPARING",
	            "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")
	            .contains(newStatus)) {
	        throw new RuntimeException("Invalid order status");
	    }

	    Map<String, String> nextStatus = Map.of(
	            "PENDING", "CONFIRMED",
	            "CONFIRMED", "PREPARING",
	            "PREPARING", "OUT_FOR_DELIVERY",
	            "OUT_FOR_DELIVERY", "DELIVERED"
	    );

	    if (!newStatus.equals("CANCELLED") &&
	            !newStatus.equals(nextStatus.get(currentStatus))) {
	        throw new RuntimeException("Invalid order status transition");
	    }

	    order.setStatus(newStatus);
	    orderRepository.save(order);

	    return "Status Changed Successfully";
	}

	public void addOrder(Order order) {
		orderRepository.save(order);
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
	}

	public String updateOrder(Order order, Long id) {

	    Order existingOrder = orderRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Order not found"));

	    existingOrder.setDeliveryAddress(order.getDeliveryAddress());
	    existingOrder.setSpecialInstructions(order.getSpecialInstructions());

	    orderRepository.save(existingOrder);

	    return "Order updated successfully";
	}

	public void deleteOrder(Long id) {
		orderRepository.deleteById(id);
	}
}