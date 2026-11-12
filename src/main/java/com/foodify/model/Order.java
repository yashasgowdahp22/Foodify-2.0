
package com.foodify.model;

import java.math.BigDecimal;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String deliveryAddress;
    private String specialInstructions;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<OrderItem> orderItems=new ArrayList<>();

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "payment_id", unique = true)
    private PaymentDetails paymentDetails;

    @OneToOne
    @JoinColumn(name = "delivery_id", unique = true)
    private Delivery delivery;

    public void addOrderItem(OrderItem orderItem) {
    	orderItems.add(orderItem);
    	orderItem.setOrder(this);
    }
    public void removeOrderItem(OrderItem orderItem) {
    	orderItems.remove(orderItem);
    	orderItem.setOrder(null);
    }
}
