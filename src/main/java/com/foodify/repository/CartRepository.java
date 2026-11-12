package com.foodify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodify.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long>{

//	Cart findByCustomerId(Long customerId);
	Optional<Cart> findByCustomerId(Long customerId);
}
