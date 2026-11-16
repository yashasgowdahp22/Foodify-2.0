package com.foodify.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String specialInstructions;

    private Long customerId;
    private String customerName;
    private String customerEmail;

    private Long restaurantId;
    private String restaurantName;

    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;

    private List<OrderItemResponseDTO> items = new ArrayList<>();
}
