
package com.foodify.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnore
    @OneToMany(mappedBy = "menuItem")
    private List<CartItem> cartItems;

    @JsonIgnore
    @OneToMany(mappedBy = "menuItem")
    private List<OrderItem> orderItems;
}
