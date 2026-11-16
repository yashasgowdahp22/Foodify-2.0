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

import com.foodify.dto.RestaurantRequestDTO;
import com.foodify.dto.RestaurantResponseDTO;
import com.foodify.service.RestaurantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Restaurant Management", description = "Restaurant APIs for Admin, Restaurant Owners, and Customers")
@RestController
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(summary = "Add a new restaurant (Admin only)")
    @PostMapping("addRestaurant")
    public ResponseEntity<String> addRestaurant(@Valid @RequestBody RestaurantRequestDTO requestDTO) {
        restaurantService.addRestaurant(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Restaurant added successfully");
    }

    @Operation(summary = "Get all restaurants (Public)")
    @GetMapping("getAllRestaurants")
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @Operation(summary = "Get restaurant by ID (Public)")
    @GetMapping("getRestaurant/{Id}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long Id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(Id));
    }

    @Operation(summary = "Search restaurants by name or address (Public)")
    @GetMapping("searchRestaurants")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurants(@RequestParam(required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(query));
    }

    @Operation(summary = "Get restaurant profile for currently logged-in restaurant owner")
    @GetMapping("myRestaurant")
    public ResponseEntity<RestaurantResponseDTO> getMyRestaurant(Authentication authentication) {
        return ResponseEntity.ok(restaurantService.getRestaurantByOwnerEmail(authentication.getName()));
    }

    @Operation(summary = "Update restaurant profile for currently logged-in restaurant owner")
    @PutMapping("myRestaurant")
    public ResponseEntity<String> updateMyRestaurant(
            Authentication authentication,
            @Valid @RequestBody RestaurantRequestDTO requestDTO) {
        String message = restaurantService.updateMyRestaurant(authentication.getName(), requestDTO);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Toggle open/closed status for currently logged-in owner's restaurant")
    @PatchMapping("myRestaurant/status")
    public ResponseEntity<String> toggleMyRestaurantStatus(
            Authentication authentication,
            @RequestParam boolean open) {
        String message = restaurantService.toggleRestaurantOpenStatus(authentication.getName(), open);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Update restaurant by ID (Admin or Restaurant Owner)")
    @PutMapping("updateRestaurant/{Id}")
    public ResponseEntity<String> updateRestaurant(
            @Valid @RequestBody RestaurantRequestDTO requestDTO,
            @PathVariable Long Id,
            Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String currentUserEmail = authentication != null ? authentication.getName() : null;

        String message = restaurantService.updateRestaurant(requestDTO, Id, currentUserEmail, isAdmin);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "Delete restaurant by ID (Admin only)")
    @DeleteMapping("deleteRestaurant/{Id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long Id) {
        restaurantService.deleteRestaurant(Id);
        return ResponseEntity.ok("Restaurant deleted successfully");
    }
}