package com.foodify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodify.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{

    List<Order> findByCustomerEmailOrderByOrderDateDesc(String email);

    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);

    List<Order> findByRestaurantRestaurantIdOrderByOrderDateDesc(Long restaurantId);

    List<Order> findByRestaurantOwnerEmailOrderByOrderDateDesc(String email);
}
