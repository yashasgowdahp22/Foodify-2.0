package com.foodify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignDeliveryRequestDTO {

    private Long deliveryPartnerId;

    private String deliveryPartnerEmail;

    private String deliveryAddress;
}
