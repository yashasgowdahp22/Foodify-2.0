package com.foodify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodify.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>{

	// Spring Data JPA automatically implements this based on the method name!
    Optional<Customer> findByEmail(String email);
    
    boolean existsByEmail(String email);

    boolean existsByRole(String role);

    List<Customer> findByRole(String role);
}
