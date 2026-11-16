package com.foodify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {

    private String deliveryAddress;

    private String specialInstructions;

    @NotBlank(message = "Payment method is required (e.g. COD, UPI, CARD)")
    private String paymentMethod;
}
