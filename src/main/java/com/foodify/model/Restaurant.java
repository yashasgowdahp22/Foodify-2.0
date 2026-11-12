
package com.foodify.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    private String restaurantName;
    private String address;
    private String contactNumber;
    private String email;
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<MenuItem> menuItems =new ArrayList<>();
    
    public void addMenuItem(MenuItem menuItem) {
    	menuItems.add(menuItem);
    	menuItem.setRestaurant(this);
    }
    public void removeMenuItem(MenuItem menuItem) {
    	menuItems.remove(menuItem);
    	menuItem.setRestaurant(null);
    }
}
