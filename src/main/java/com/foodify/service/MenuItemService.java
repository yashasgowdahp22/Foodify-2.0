
package com.foodify.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.foodify.dto.MenuItemRequestDTO;
import com.foodify.dto.MenuItemResponseDTO;
import com.foodify.exception.BadRequestException;
import com.foodify.exception.ResourceNotFoundException;
import com.foodify.model.MenuItem;
import com.foodify.model.Restaurant;
import com.foodify.repository.MenuItemRepository;
import com.foodify.repository.RestaurantRepository;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(
            MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // Add Menu Item for currently authenticated Owner or Admin
    public MenuItemResponseDTO addMenuItem(MenuItemRequestDTO requestDTO, String userEmail, boolean isAdmin) {
        Restaurant restaurant;

        if (isAdmin) {
            if (requestDTO.getRestaurantId() == null) {
                throw new BadRequestException("Restaurant ID is required when adding a menu item as Admin");
            }
            restaurant = restaurantRepository.findById(requestDTO.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + requestDTO.getRestaurantId()));
        } else {
            restaurant = restaurantRepository.findByOwnerEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("No restaurant found for owner: " + userEmail));
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(requestDTO.getName());
        menuItem.setDescription(requestDTO.getDescription());
        menuItem.setPrice(requestDTO.getPrice());
        menuItem.setCategory(requestDTO.getCategory());
        menuItem.setImageUrl(requestDTO.getImageUrl());
        menuItem.setAvailable(requestDTO.isAvailable());
        menuItem.setRestaurant(restaurant);

        MenuItem saved = menuItemRepository.save(menuItem);
        return mapToResponseDTO(saved);
    }

    // Backward-compatible entity-based add
    public void addMenuItem(MenuItem menuItem) {
        if (menuItem.getRestaurant() == null ||
                menuItem.getRestaurant().getRestaurantId() == null) {
            throw new BadRequestException("Restaurant ID is missing in request");
        }

        Long restaurantId = menuItem.getRestaurant().getRestaurantId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        menuItem.setRestaurant(restaurant);
        menuItemRepository.save(menuItem);
    }

    // Public: Get all menu items for a specific restaurant
    public List<MenuItemResponseDTO> getMenuItemsByRestaurant(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }
        return menuItemRepository.findByRestaurantRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Owner: Get all menu items for the logged-in owner's restaurant
    public List<MenuItemResponseDTO> getMyRestaurantMenuItems(String ownerEmail) {
        Restaurant restaurant = restaurantRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant found for owner: " + ownerEmail));

        return menuItemRepository.findByRestaurantRestaurantId(restaurant.getRestaurantId())
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Public: Search menu items by dish name
    public List<MenuItemResponseDTO> searchMenuItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMenuItems();
        }
        return menuItemRepository.findByNameContainingIgnoreCase(query.trim())
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Public: Filter menu items by category
    public List<MenuItemResponseDTO> getMenuItemsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllMenuItems();
        }
        return menuItemRepository.findByCategoryIgnoreCase(category.trim())
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get all menu items across all restaurants
    public List<MenuItemResponseDTO> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Backward-compatible entity-returning method
    public List<MenuItem> getAllMenuItem() {
        return menuItemRepository.findAll();
    }

    public MenuItemResponseDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));
        return mapToResponseDTO(menuItem);
    }

    // Backward-compatible method
    public MenuItem getMenuItem(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));
    }

    // Update Menu Item with ownership verification
    public String updateMenuItem(Long id, MenuItemRequestDTO dto, String userEmail, boolean isAdmin) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));

        // Ownership check
        verifyMenuItemOwnership(existingMenuItem, userEmail, isAdmin);

        existingMenuItem.setName(dto.getName());
        existingMenuItem.setDescription(dto.getDescription());
        existingMenuItem.setPrice(dto.getPrice());
        existingMenuItem.setCategory(dto.getCategory());
        existingMenuItem.setImageUrl(dto.getImageUrl());
        existingMenuItem.setAvailable(dto.isAvailable());

        menuItemRepository.save(existingMenuItem);
        return "MenuItem updated successfully";
    }

    // Backward-compatible update
    public String updateMenuItem(MenuItem menuItem, Long id) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));

        existingMenuItem.setName(menuItem.getName());
        existingMenuItem.setDescription(menuItem.getDescription());
        existingMenuItem.setPrice(menuItem.getPrice());
        existingMenuItem.setCategory(menuItem.getCategory());
        existingMenuItem.setImageUrl(menuItem.getImageUrl());
        existingMenuItem.setAvailable(menuItem.isAvailable());

        menuItemRepository.save(existingMenuItem);
        return "MenuItem updated successfully";
    }

    // Toggle item availability with ownership check
    public String toggleMenuItemAvailability(Long id, boolean available, String userEmail, boolean isAdmin) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));

        verifyMenuItemOwnership(existingMenuItem, userEmail, isAdmin);

        existingMenuItem.setAvailable(available);
        menuItemRepository.save(existingMenuItem);
        return "MenuItem is now " + (available ? "AVAILABLE" : "UNAVAILABLE");
    }

    // Delete Menu Item with ownership verification
    public void deleteMenuItem(Long id, String userEmail, boolean isAdmin) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));

        verifyMenuItemOwnership(existingMenuItem, userEmail, isAdmin);

        menuItemRepository.delete(existingMenuItem);
    }

    // Backward-compatible delete
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("MenuItem not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    // Helper: verify user is either ADMIN or the owner of the restaurant to which this menu item belongs
    private void verifyMenuItemOwnership(MenuItem menuItem, String userEmail, boolean isAdmin) {
        if (isAdmin) {
            return;
        }

        Restaurant restaurant = menuItem.getRestaurant();
        if (restaurant == null || restaurant.getOwner() == null ||
                !restaurant.getOwner().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("Access denied: You do not own the restaurant for this menu item");
        }
    }

    public MenuItemResponseDTO mapToResponseDTO(MenuItem menuItem) {
        Long restaurantId = null;
        String restaurantName = null;

        if (menuItem.getRestaurant() != null) {
            restaurantId = menuItem.getRestaurant().getRestaurantId();
            restaurantName = menuItem.getRestaurant().getRestaurantName();
        }

        return new MenuItemResponseDTO(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategory(),
                menuItem.getImageUrl(),
                menuItem.isAvailable(),
                restaurantId,
                restaurantName
        );
    }
}
