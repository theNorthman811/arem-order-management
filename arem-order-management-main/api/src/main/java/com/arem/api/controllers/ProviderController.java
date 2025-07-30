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

import com.arem.api.providers.ProviderProvider;
import com.arem.productInput.contracts.ProviderContract;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class ProviderController {
	
	@Autowired
	private ProviderProvider providerProvider;
		
	@GetMapping("/providers")
	public ResponseEntity<List<ProviderContract>> getProviders() {
		return ResponseEntity.ok(providerProvider.getProviders());
	}
	
	@GetMapping("/provider/{id}")
	public ResponseEntity<ProviderContract> getProviderById(@PathVariable long id) {
		ProviderContract provider = providerProvider.getProviderById(id);
		if (provider == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(provider);
	}
	
	@PostMapping("/provider")
	public ResponseEntity<ProviderContract> save(@Valid @RequestBody ProviderContract contract) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED).body(providerProvider.save(contract));
	}
	
	@PutMapping("/provider/{id}")
	public ResponseEntity<ProviderContract> update(@PathVariable long id, @Valid @RequestBody ProviderContract contract) throws Exception {
		return ResponseEntity.ok(providerProvider.update(id, contract));
	}
	
	@DeleteMapping("/provider/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) throws Exception {
		providerProvider.delete(id);
		return ResponseEntity.noContent().build();
	}
}
