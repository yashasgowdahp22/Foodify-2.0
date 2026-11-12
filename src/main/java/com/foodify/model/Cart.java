
package com.foodify.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    
    

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;

//    @JsonIgnore
    @OneToMany(mappedBy = "cart",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<CartItem> cartItems = new ArrayList<>();    
    
    public void addCartItems(CartItem cartItem) {
    	cartItems.add(cartItem);
    	cartItem.setCart(this);
    }
    public void removeCartItem(CartItem cartItem) {
    	cartItems.remove(cartItem);
    	cartItem.setCart(null);
    }
}
