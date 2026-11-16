
package com.foodify.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.dto.ChangePasswordRequest;
import com.foodify.dto.CreateUserRequest;
import com.foodify.dto.LoginRequest;
import com.foodify.dto.LoginResponse;
import com.foodify.dto.RegisterRequest;
import com.foodify.dto.UserResponseDTO;
import com.foodify.exception.ResourceNotFoundException;
import com.foodify.model.Customer;
import com.foodify.security.JwtService;
import com.foodify.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Registration, Login, Profile and Password APIs")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerService customerService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomerService customerService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customerService = customerService;
    }

    // Customer Registration
    @Operation(summary = "Register a new customer")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        Customer customer = new Customer();

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setPassword(request.getPassword());

        customerService.addCustomer(customer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Customer registered successfully");
    }

    // User Login (works for CUSTOMER, ADMIN, RESTAURANT, DELIVERY)
    @Operation(summary = "Login and receive JWT token with user role")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Customer customer = customerService.getCustomerByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String role = customer.getRole();
        if (role == null || role.isBlank()) {
            role = "CUSTOMER";
        }

        String token = jwtService.generateToken(customer);

        LoginResponse response = new LoginResponse(
                token,
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                role.toUpperCase()
        );

        return ResponseEntity.ok(response);
    }

    // Get Current Logged-In User
    @Operation(summary = "Get currently authenticated user details")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        UserResponseDTO user = customerService.getUserProfileByEmail(authentication.getName());
        return ResponseEntity.ok(user);
    }

    // Change Current User Password
    @Operation(summary = "Change password for currently authenticated user")
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        customerService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok("Password updated successfully");
    }

    // Stateless Logout
    @Operation(summary = "Logout current user (client discards JWT token)")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully. Please remove the token from client storage.");
    }

    // Admin Creates Staff/Role Accounts (RESTAURANT, DELIVERY, ADMIN, CUSTOMER)
    @Operation(summary = "Admin creates a user with a specific role")
    @PostMapping("/admin/create-user")
    public ResponseEntity<UserResponseDTO> createUserByAdmin(@Valid @RequestBody CreateUserRequest request) {
        UserResponseDTO createdUser = customerService.createUserWithRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
