
package com.foodify.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodify.dto.ChangePasswordRequest;
import com.foodify.dto.CreateUserRequest;
import com.foodify.dto.UserResponseDTO;
import com.foodify.exception.BadRequestException;
import com.foodify.exception.ConflictException;
import com.foodify.exception.ResourceNotFoundException;
import com.foodify.model.Customer;
import com.foodify.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {

        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // Customer Registration (always assigns CUSTOMER role)
    public void addCustomer(Customer customer) {

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        customer.setPassword(
                passwordEncoder.encode(customer.getPassword()));

        customer.setRole("CUSTOMER");

        customerRepository.save(customer);
    }

    // Admin creates user with a specific role (CUSTOMER, ADMIN, RESTAURANT, DELIVERY)
    public UserResponseDTO createUserWithRole(CreateUserRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        String normalizedRole = request.getRole().trim().toUpperCase();
        List<String> allowedRoles = List.of("CUSTOMER", "ADMIN", "RESTAURANT", "DELIVERY");

        if (!allowedRoles.contains(normalizedRole)) {
            throw new BadRequestException("Invalid role. Allowed roles: CUSTOMER, ADMIN, RESTAURANT, DELIVERY");
        }

        Customer user = new Customer();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(normalizedRole);

        Customer savedUser = customerRepository.save(user);

        return mapToUserResponseDTO(savedUser);
    }

    public UserResponseDTO getUserProfileByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return mapToUserResponseDTO(customer);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), customer.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);
    }

    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Customer not found with id: " + id));
    }

    public String updateCustomer(Customer customer, Long id) {

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Customer not found"));

        existingCustomer.setName(customer.getName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setAddress(customer.getAddress());

        customerRepository.save(existingCustomer);

        return "Customer updated successfully";
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    private UserResponseDTO mapToUserResponseDTO(Customer customer) {
        String role = customer.getRole();
        if (role == null || role.isBlank()) {
            role = "CUSTOMER";
        }
        return new UserResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAddress(),
                role.toUpperCase()
        );
    }
}
