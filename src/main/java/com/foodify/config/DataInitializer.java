package com.foodify.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.foodify.model.Customer;
import com.foodify.model.Restaurant;
import com.foodify.repository.CustomerRepository;
import com.foodify.repository.RestaurantRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            CustomerRepository customerRepository,
            RestaurantRepository restaurantRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Safely create a default Admin only if no ADMIN account exists in the database
        if (!customerRepository.existsByRole("ADMIN")
                && !customerRepository.existsByEmail("admin@foodify.com")) {

            Customer admin = new Customer();
            admin.setName("Foodify Admin");
            admin.setEmail("admin@foodify.com");
            admin.setAddress("Foodify HQ");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole("ADMIN");

            customerRepository.save(admin);
            System.out.println("Default ADMIN account created: admin@foodify.com");
        }

        // Ensure any existing restaurant records in the DB have open = true if uninitialized
        List<Restaurant> existing = restaurantRepository.findAll();
        for (Restaurant r : existing) {
            // If the column was just added and defaulted to false in old DB state, initialize to true
            if (!r.isOpen() && r.getOwner() == null) {
                r.setOpen(true);
                restaurantRepository.save(r);
            }
        }
    }
}
