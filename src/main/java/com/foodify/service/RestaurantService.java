package com.foodify.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodify.dto.RestaurantRequestDTO;
import com.foodify.dto.RestaurantResponseDTO;
import com.foodify.model.Restaurant;
import com.foodify.repository.RestaurantRepository;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    // Add Restaurant (Takes RequestDTO, saves Entity)
    public void addRestaurant(RestaurantRequestDTO requestDTO) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(requestDTO.getRestaurantName());
        restaurant.setAddress(requestDTO.getAddress());
        restaurant.setContactNumber(requestDTO.getContactNumber());
        restaurant.setEmail(requestDTO.getEmail());
        restaurant.setDescription(requestDTO.getDescription());

        restaurantRepository.save(restaurant);
    }

    // Get All Restaurants (Converts List of Entities -> List of ResponseDTOs)
    public List<RestaurantResponseDTO> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get Restaurant By ID (Returns ResponseDTO, uses your Global Exception Handler if missing)
    public RestaurantResponseDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        return mapToResponseDTO(restaurant);
    }

    // Update Restaurant
    public String updateRestaurant(RestaurantRequestDTO requestDTO, Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        restaurant.setRestaurantName(requestDTO.getRestaurantName());
        restaurant.setAddress(requestDTO.getAddress());
        restaurant.setContactNumber(requestDTO.getContactNumber());
        restaurant.setEmail(requestDTO.getEmail());
        restaurant.setDescription(requestDTO.getDescription());

        restaurantRepository.save(restaurant);
        return "Restaurant updated successfully";
    }

    // Delete Restaurant
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found with id: " + id);
        }
        restaurantRepository.deleteById(id);
    }

    // Helper method to convert Entity to ResponseDTO
    private RestaurantResponseDTO mapToResponseDTO(Restaurant restaurant) {
        return new RestaurantResponseDTO(
                restaurant.getRestaurantId(),
                restaurant.getRestaurantName(),
                restaurant.getAddress(),
                restaurant.getContactNumber(),
                restaurant.getEmail(),
                restaurant.getDescription()
        );
    }
}