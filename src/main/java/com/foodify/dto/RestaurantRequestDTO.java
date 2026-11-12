package com.foodify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestaurantRequestDTO {
	
	@NotBlank(message="Restaurnat name must not be null")
	@Size(min=3,max=100,message="Restaurant name must be between 2 to 100 characters")
    private String restaurantName;
	
	@NotBlank(message="Address is required")
    private String address;
	
	@NotBlank(message="Contact Number is required")
	@Pattern(regexp = "^[0-9]{10}$" , message="Contact number must be a valid 10-digit number")
    private String contactNumber;
	
	@NotBlank(message="Email is required")
	@Email(message="Invalid email format")
    private String email;
	
    private String description;
    
    
	public String getRestaurantName() {
		return restaurantName;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
    
    
    

}
