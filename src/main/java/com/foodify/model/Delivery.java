
package com.foodify.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deliveryStatus;
    private String deliveryAddress;
    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
    private String trackingNumber;

    @OneToOne(mappedBy = "delivery")
    @JsonIgnore
    private Order order;
}
