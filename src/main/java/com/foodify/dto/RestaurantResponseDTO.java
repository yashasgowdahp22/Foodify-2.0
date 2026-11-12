package com.foodify.dto;

public class RestaurantResponseDTO {
    private Long id;
    private String restaurantName;
    private String address;
    private String contactNumber;
    private String email;
    private String description;

    public RestaurantResponseDTO(Long id, String restaurantName, String address, String contactNumber, String email, String description) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}