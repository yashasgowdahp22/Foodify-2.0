package com.foodify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.dto.RestaurantRequestDTO;
import com.foodify.dto.RestaurantResponseDTO;
import com.foodify.service.RestaurantService;

import jakarta.validation.Valid;

@RestController
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("addRestaurant")
    public void addRestaurant(@Valid @RequestBody RestaurantRequestDTO requestDTO) {
        restaurantService.addRestaurant(requestDTO);
    }

    @GetMapping("getAllRestaurants")
    public List<RestaurantResponseDTO> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("getRestaurant/{Id}")
    public RestaurantResponseDTO getRestaurantById(@PathVariable Long Id) {
        return restaurantService.getRestaurantById(Id);
    }

    @PutMapping("updateRestaurant/{Id}")
    public String updateRestaurant(@RequestBody RestaurantRequestDTO requestDTO, @PathVariable Long Id) {
        return restaurantService.updateRestaurant(requestDTO, Id);
    }

    @DeleteMapping("deleteRestaurant/{Id}")
    public void deleteRestaurant(@PathVariable Long Id) {
        restaurantService.deleteRestaurant(Id);
    }
}