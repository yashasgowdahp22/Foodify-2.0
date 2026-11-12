
package com.foodify.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.model.MenuItem;
import com.foodify.model.Restaurant;
import com.foodify.repository.MenuItemRepository;
import com.foodify.repository.RestaurantRepository;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public void addMenuItem(MenuItem menuItem) {

        if (menuItem.getRestaurant() == null ||
                menuItem.getRestaurant().getRestaurantId() == null) {
            throw new RuntimeException("Restaurant ID is missing in request");
        }

        Long restaurantId = menuItem.getRestaurant().getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                    new RuntimeException("Restaurant not found"));

        menuItem.setRestaurant(restaurant);

        menuItemRepository.save(menuItem);
    }
    public List<MenuItem> getAllMenuItem() {
        return menuItemRepository.findAll();
    }

    public MenuItem getMenuItem(Long Id) {
        return menuItemRepository.findById(Id)
                .orElseThrow(() ->
                    new RuntimeException("MenuItem not found " + Id));
    }

    public String updateMenuItem(MenuItem menuItem, Long Id) {

        MenuItem existingMenuItem = menuItemRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));

        existingMenuItem.setName(menuItem.getName());
        existingMenuItem.setDescription(menuItem.getDescription());
        existingMenuItem.setPrice(menuItem.getPrice());
        existingMenuItem.setCategory(menuItem.getCategory());
        existingMenuItem.setImageUrl(menuItem.getImageUrl());
        existingMenuItem.setAvailable(menuItem.isAvailable());

        menuItemRepository.save(existingMenuItem);

        return "MenuItem updated successfully";
    }

    public void deleteMenuItem(Long Id) {
        menuItemRepository.deleteById(Id);
    }
}
