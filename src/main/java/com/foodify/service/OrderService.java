package com.foodify.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.model.Cart;
import com.foodify.model.CartItem;
import com.foodify.model.Customer;
import com.foodify.model.Order;
import com.foodify.model.OrderItem;
import com.foodify.repository.CartRepository;
import com.foodify.repository.CustomerRepository;
import com.foodify.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private  CustomerRepository customerRepository;

	@Autowired
	private CartRepository cartRepository;


	@Transactional
	public void placeOrder(Long customerId) {
		Customer customer=customerRepository.findById(customerId)
				.orElseThrow(()-> new RuntimeException("customer not found"));

		Cart cart=cartRepository.findByCustomerId(customerId)
				.orElseThrow(()->new RuntimeException("cart not found"));

		if(cart.getCartItems().isEmpty()) {
			throw new RuntimeException("cart is  empty");
		}

		Order order =new Order();

		order.setCustomer(customer);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus("pending");
		order.setDeliveryAddress(customer.getAddress());
		BigDecimal totalAmount = BigDecimal.ZERO;

		for(CartItem cartItem:cart.getCartItems()) {
			OrderItem orderItem=new OrderItem();

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

	    // Check whether order is already completed
	    if (currentStatus.equals("DELIVERED") ||
	            currentStatus.equals("CANCELLED")) {
	        throw new RuntimeException("Order already completed");
	    }

	    // Validate the requested status
	    if (!List.of("PENDING", "CONFIRMED", "PREPARING",
	            "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")
	            .contains(newStatus)) {
	        throw new RuntimeException("Invalid order status");
	    }

	    // Define the allowed status sequence
	    Map<String, String> nextStatus = Map.of(
	            "PENDING", "CONFIRMED",
	            "CONFIRMED", "PREPARING",
	            "PREPARING", "OUT_FOR_DELIVERY",
	            "OUT_FOR_DELIVERY", "DELIVERED"
	    );

	    // Check valid status transition
	    if (!newStatus.equals("CANCELLED") &&
	            !newStatus.equals(nextStatus.get(currentStatus))) {
	        throw new RuntimeException("Invalid order status transition");
	    }

	    // Update order status
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