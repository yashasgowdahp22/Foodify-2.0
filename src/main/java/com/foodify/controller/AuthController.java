
package com.foodify.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.dto.LoginRequest;
import com.foodify.dto.LoginResponse;
import com.foodify.dto.RegisterRequest;
import com.foodify.model.Customer;
import com.foodify.security.JwtService;
import com.foodify.service.CustomerService;

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
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        Customer customer = new Customer();

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setPassword(request.getPassword());

        customerService.addCustomer(customer);

        return "Customer registered successfully";
    }

    // Customer Login
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(request.getEmail());

        return new LoginResponse(token);
    }
}
