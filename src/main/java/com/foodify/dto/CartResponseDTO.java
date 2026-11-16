package com.foodify.dto;

import java.math.BigDecimal;
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
public class CartResponseDTO {

    private Long cartId;
    private Long customerId;
    private String customerName;
    private Long restaurantId;
    private String restaurantName;
    private BigDecimal totalAmount;
    private int itemCount;
    private List<CartItemResponseDTO> items = new ArrayList<>();
}
