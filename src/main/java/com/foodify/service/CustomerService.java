
package com.foodify.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // Customer Registration
    public void addCustomer(Customer customer) {

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        customer.setPassword(
                passwordEncoder.encode(customer.getPassword()));

        customer.setRole("CUSTOMER");

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
}
