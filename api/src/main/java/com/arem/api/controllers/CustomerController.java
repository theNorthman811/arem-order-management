package com.arem.api.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arem.api.providers.CustomerProvider;
import com.arem.productInput.contracts.CustomerContract;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class CustomerController {
	
	@Autowired
	private CustomerProvider customerProvider;
		
	@GetMapping("/customers")
	public ResponseEntity<List<CustomerContract>> getCustomers() {
		return ResponseEntity.ok(customerProvider.getCustomers());
	}
	
	@GetMapping("/customer/{id}")
	public ResponseEntity<CustomerContract> getCustomerById(@PathVariable long id) {
		CustomerContract customer = customerProvider.getCustomerById(id);
		if (customer == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(customer);
	}
	
	@PostMapping("/customer")
	public ResponseEntity<CustomerContract> save(@Valid @RequestBody CustomerContract contract) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED).body(customerProvider.save(contract));
	}
	
	@PutMapping("/customer/{id}")
	public ResponseEntity<CustomerContract> update(@PathVariable long id, @Valid @RequestBody CustomerContract contract) throws Exception {
		return ResponseEntity.ok(customerProvider.update(id, contract));
	}
	
	@DeleteMapping("/customer/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) throws Exception {
		customerProvider.delete(id);
		return ResponseEntity.noContent().build();
	}
}
