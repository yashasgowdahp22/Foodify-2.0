package com.foodify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodify.model.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantRestaurantId(Long restaurantId);

    List<MenuItem> findByRestaurantRestaurantIdAndAvailableTrue(Long restaurantId);

    List<MenuItem> findByNameContainingIgnoreCase(String name);

    List<MenuItem> findByCategoryIgnoreCase(String category);

    List<MenuItem> findByRestaurantRestaurantIdAndCategoryIgnoreCase(Long restaurantId, String category);
}
