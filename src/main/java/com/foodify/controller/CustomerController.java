package com.foodify.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.model.Customer;
import com.foodify.service.CustomerService;

import jakarta.validation.Valid;

@RestController
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@GetMapping("getCustomerByEmail")
	public Optional<Customer> getCustomerByEmail(String email) {
		Optional<Customer> findByEmail=customerService.getCustomerByEmail(email);
		return findByEmail;
	}
	@PostMapping("addCustomer")
	public void addCustomer(@Valid  @RequestBody Customer customer) {
		customerService.addCustomer(customer);
	}
	
	@GetMapping("getAllCustomer")
	public List<Customer> getAllCustomer(){
		List<Customer> customer = customerService.getAllCustomer();
		return customer;
	}
	
	@GetMapping("getCustomerById/{Id}")
	public Customer getCustomerById(@PathVariable Long Id) {
		 Customer customer = customerService.getCustomerById(Id);
		 return customer;
	}
	
	@PutMapping("updateCustomer/{Id}")
	public String updateCustomer(@RequestBody Customer customer,@PathVariable Long Id) {
		String update=customerService.updateCustomer(customer, Id);
		return update;
	}
	
	@DeleteMapping("deleteCustomer/{Id}")
	public void deleteCustomer(@PathVariable Long Id) {
		customerService.deleteCustomer(Id);
	}
	
	
}
