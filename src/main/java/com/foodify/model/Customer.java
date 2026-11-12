
package com.foodify.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private String address;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    private String role;

    @OneToOne(mappedBy = "customer",
              cascade = CascadeType.ALL,
              orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "customer",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
    
    
    public void addOrder(Order order) {

        orders.add(order);

        order.setCustomer(this);
    }
    
    public void deleteOrder(Order order) {
    	orders.remove(order);
    	order.setCustomer(null);
    }
    
    
}
