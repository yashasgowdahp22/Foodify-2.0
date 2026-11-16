package com.foodify.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {

    private Long deliveryId;
    private String deliveryStatus;
    private String trackingNumber;
    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
    private String deliveryAddress;

    private Long orderId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;

    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;

    private Long customerId;
    private String customerName;
    private String customerAddress;

    private Long deliveryPartnerId;
    private String deliveryPartnerName;
    private String deliveryPartnerEmail;
}
