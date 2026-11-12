package com.foodify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.foodify.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Custom declaration to match what your trainer asked for (returning raw CartItem instead of Optional)
//    CartItem findById(Long id);
}