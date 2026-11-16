package com.foodify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodify.model.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long>{

    List<Delivery> findByDeliveryPartnerEmailOrderByAssignedAtDesc(String email);

    List<Delivery> findByDeliveryPartnerIdOrderByAssignedAtDesc(Long partnerId);

    Optional<Delivery> findByOrderId(Long orderId);
}
