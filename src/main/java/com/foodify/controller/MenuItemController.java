package com.foodify.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.dto.MenuItemRequestDTO;
import com.foodify.dto.MenuItemResponseDTO;
import com.foodify.service.MenuItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Menu Management", description = "Menu item APIs for Customers, Restaurant Owners, and Admin")
@RestController
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @Operation(summary = "Add a new menu item (Restaurant Owner or Admin)")
    @PostMapping("addMenuItem")
    public ResponseEntity<MenuItemResponseDTO> addMenuItem(
            @Valid @RequestBody MenuItemRequestDTO requestDTO,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String userEmail = authentication != null ? authentication.getName() : null;

        MenuItemResponseDTO created = menuItemService.addMenuItem(requestDTO, userEmail, isAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get all menu items across all restaurants (Public)")
    @GetMapping("getAllMenuItems")
    public ResponseEntity<List<MenuItemResponseDTO>> getAllMenuItem() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    @Operation(summary = "Get menu item by ID (Public)")
    @GetMapping("getMenuItem/{Id}")
    public ResponseEntity<MenuItemResponseDTO> getMenuItem(@PathVariable Long Id) {
        return ResponseEntity.ok(menuItemService.getMenuItemById(Id));
    }

    @Operation(summary = "Get menu items for a specific restaurant (Public)")
    @GetMapping("getRestaurantMenu/{restaurantId}")
    public ResponseEntity<List<MenuItemResponseDTO>> getRestaurantMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByRestaurant(restaurantId));
    }

    @Operation(summary = "Get menu items for currently logged-in restaurant owner's restaurant")
    @GetMapping("myRestaurant/menu")
    public ResponseEntity<List<MenuItemResponseDTO>> getMyRestaurantMenu(Authentication authentication) {
        return ResponseEntity.ok(menuItemService.getMyRestaurantMenuItems(authentication.getName()));
    }

    @Operation(summary = "Search menu items by dish name (Public)")
    @GetMapping("searchMenuItems")
    public ResponseEntity<List<MenuItemResponseDTO>> searchMenuItems(@RequestParam(required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(menuItemService.searchMenuItems(query));
    }

    @Operation(summary = "Get menu items by category (Public)")
    @GetMapping("getMenuItemsByCategory/{category}")
    public ResponseEntity<List<MenuItemResponseDTO>> getMenuItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByCategory(category));
    }

    @Operation(summary = "Update menu item by ID (Restaurant Owner or Admin)")
    @PutMapping("updateMenuItem/{Id}")
    public ResponseEntity<String> updateMenuItem(
            @Valid @RequestBody MenuItemRequestDTO requestDTO,
            @PathVariable Long Id,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String userEmail = authentication != null ? authentication.getName() : null;

        String message = menuItemService.updateMenuItem(Id, requestDTO, userEmail, isAdmin);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Toggle menu item availability (Restaurant Owner or Admin)")
    @PatchMapping("updateMenuItemStatus/{Id}")
    public ResponseEntity<String> toggleMenuItemAvailability(
            @PathVariable Long Id,
            @RequestParam boolean available,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String userEmail = authentication != null ? authentication.getName() : null;

        String message = menuItemService.toggleMenuItemAvailability(Id, available, userEmail, isAdmin);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Delete menu item by ID (Restaurant Owner or Admin)")
    @DeleteMapping("deleteMenuItem/{Id}")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable Long Id,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String userEmail = authentication != null ? authentication.getName() : null;

        menuItemService.deleteMenuItem(Id, userEmail, isAdmin);
        return ResponseEntity.ok("MenuItem deleted successfully");
    }
}