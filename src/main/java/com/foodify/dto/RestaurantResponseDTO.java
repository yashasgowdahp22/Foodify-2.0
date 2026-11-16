package com.foodify.dto;

public class RestaurantResponseDTO {
    private Long id;
    private String restaurantName;
    private String address;
    private String contactNumber;
    private String email;
    private String description;
    private boolean open;
    private String imageUrl;
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;

    public RestaurantResponseDTO() {
    }

    public RestaurantResponseDTO(Long id, String restaurantName, String address, String contactNumber, String email, String description) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.description = description;
        this.open = true;
    }

    public RestaurantResponseDTO(Long id, String restaurantName, String address, String contactNumber,
                                 String email, String description, boolean open, String imageUrl,
                                 Long ownerId, String ownerName, String ownerEmail) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.description = description;
        this.open = open;
        this.imageUrl = imageUrl;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
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
    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}