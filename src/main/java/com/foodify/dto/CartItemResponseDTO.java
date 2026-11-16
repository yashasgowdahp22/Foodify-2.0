package com.foodify.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

    private Long cartItemId;
    private Long menuItemId;
    private String menuItemName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subTotal;
    private String category;
    private String imageUrl;
    private boolean available;
}
