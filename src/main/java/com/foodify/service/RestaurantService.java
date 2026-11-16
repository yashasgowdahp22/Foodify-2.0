package com.foodify.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.foodify.dto.RestaurantRequestDTO;
import com.foodify.dto.RestaurantResponseDTO;
import com.foodify.exception.BadRequestException;
import com.foodify.exception.ConflictException;
import com.foodify.exception.ResourceNotFoundException;
import com.foodify.model.Customer;
import com.foodify.model.Restaurant;
import com.foodify.repository.CustomerRepository;
import com.foodify.repository.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            CustomerRepository customerRepository) {
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
    }

    // Add Restaurant (Takes RequestDTO, optionally links RESTAURANT owner, saves Entity)
    public void addRestaurant(RestaurantRequestDTO requestDTO) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(requestDTO.getRestaurantName());
        restaurant.setAddress(requestDTO.getAddress());
        restaurant.setContactNumber(requestDTO.getContactNumber());
        restaurant.setEmail(requestDTO.getEmail());
        restaurant.setDescription(requestDTO.getDescription());
        restaurant.setOpen(requestDTO.isOpen());
        restaurant.setImageUrl(requestDTO.getImageUrl());

        if (requestDTO.getOwnerEmail() != null && !requestDTO.getOwnerEmail().isBlank()) {
            Customer owner = customerRepository.findByEmail(requestDTO.getOwnerEmail().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner account not found with email: " + requestDTO.getOwnerEmail()));

            if (restaurantRepository.existsByOwnerId(owner.getId())) {
                throw new ConflictException("Owner already owns a restaurant: " + requestDTO.getOwnerEmail());
            }
            restaurant.setOwner(owner);
        }

        restaurantRepository.save(restaurant);
    }

    // Get All Restaurants (Converts List of Entities -> List of ResponseDTOs)
    public List<RestaurantResponseDTO> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get Restaurant By ID
    public RestaurantResponseDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        return mapToResponseDTO(restaurant);
    }

    // Search Restaurants by name or address
    public List<RestaurantResponseDTO> searchRestaurants(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllRestaurants();
        }
        String cleanQuery = query.trim();
        List<Restaurant> restaurants = restaurantRepository
                .findByRestaurantNameContainingIgnoreCaseOrAddressContainingIgnoreCase(cleanQuery, cleanQuery);

        return restaurants.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get My Restaurant (for authenticated RESTAURANT owner)
    public RestaurantResponseDTO getRestaurantByOwnerEmail(String ownerEmail) {
        Restaurant restaurant = restaurantRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant registered for owner: " + ownerEmail));
        return mapToResponseDTO(restaurant);
    }

    // Update My Restaurant (for authenticated RESTAURANT owner)
    public String updateMyRestaurant(String ownerEmail, RestaurantRequestDTO requestDTO) {
        Restaurant restaurant = restaurantRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant registered for owner: " + ownerEmail));

        restaurant.setRestaurantName(requestDTO.getRestaurantName());
        restaurant.setAddress(requestDTO.getAddress());
        restaurant.setContactNumber(requestDTO.getContactNumber());
        restaurant.setEmail(requestDTO.getEmail());
        restaurant.setDescription(requestDTO.getDescription());
        restaurant.setOpen(requestDTO.isOpen());
        if (requestDTO.getImageUrl() != null) {
            restaurant.setImageUrl(requestDTO.getImageUrl());
        }

        restaurantRepository.save(restaurant);
        return "Restaurant updated successfully";
    }

    // Toggle open/closed status for owner's restaurant
    public String toggleRestaurantOpenStatus(String ownerEmail, boolean open) {
        Restaurant restaurant = restaurantRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No restaurant registered for owner: " + ownerEmail));

        restaurant.setOpen(open);
        restaurantRepository.save(restaurant);
        return "Restaurant is now " + (open ? "OPEN" : "CLOSED");
    }

    // Update Restaurant with ownership verification
    public String updateRestaurant(RestaurantRequestDTO requestDTO, Long id, String currentUserEmail, boolean isAdmin) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        // Ownership verification: If not admin, the current user MUST be the restaurant's owner
        if (!isAdmin) {
            if (restaurant.getOwner() == null || !restaurant.getOwner().getEmail().equalsIgnoreCase(currentUserEmail)) {
                throw new AccessDeniedException("Access denied: You do not have permission to update this restaurant");
            }
        }

        restaurant.setRestaurantName(requestDTO.getRestaurantName());
        restaurant.setAddress(requestDTO.getAddress());
        restaurant.setContactNumber(requestDTO.getContactNumber());
        restaurant.setEmail(requestDTO.getEmail());
        restaurant.setDescription(requestDTO.getDescription());
        restaurant.setOpen(requestDTO.isOpen());
        if (requestDTO.getImageUrl() != null) {
            restaurant.setImageUrl(requestDTO.getImageUrl());
        }

        if (isAdmin && requestDTO.getOwnerEmail() != null && !requestDTO.getOwnerEmail().isBlank()) {
            Customer newOwner = customerRepository.findByEmail(requestDTO.getOwnerEmail().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + requestDTO.getOwnerEmail()));
            restaurant.setOwner(newOwner);
        }

        restaurantRepository.save(restaurant);
        return "Restaurant updated successfully";
    }

    // Backward-compatible update
    public String updateRestaurant(RestaurantRequestDTO requestDTO, Long id) {
        return updateRestaurant(requestDTO, id, null, true);
    }

    // Delete Restaurant
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + id);
        }
        restaurantRepository.deleteById(id);
    }

    // Helper method to convert Entity to ResponseDTO
    public RestaurantResponseDTO mapToResponseDTO(Restaurant restaurant) {
        Long ownerId = null;
        String ownerName = null;
        String ownerEmail = null;

        if (restaurant.getOwner() != null) {
            ownerId = restaurant.getOwner().getId();
            ownerName = restaurant.getOwner().getName();
            ownerEmail = restaurant.getOwner().getEmail();
        }

        return new RestaurantResponseDTO(
                restaurant.getRestaurantId(),
                restaurant.getRestaurantName(),
                restaurant.getAddress(),
                restaurant.getContactNumber(),
                restaurant.getEmail(),
                restaurant.getDescription(),
                restaurant.isOpen(),
                restaurant.getImageUrl(),
                ownerId,
                ownerName,
                ownerEmail
        );
    }
}