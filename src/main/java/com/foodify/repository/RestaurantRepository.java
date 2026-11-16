package com.foodify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodify.model.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByOwnerEmail(String email);

    Optional<Restaurant> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    List<Restaurant> findByRestaurantNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);

    List<Restaurant> findByOpenTrue();
}
