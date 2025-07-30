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

import com.arem.api.providers.SellerProvider;
import com.arem.productInput.contracts.SellerContract;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class SellerController {
	
	@Autowired
	private SellerProvider sellerProvider;
		
	@GetMapping("/sellers")
	public ResponseEntity<List<SellerContract>> getSellers() {
		return ResponseEntity.ok(sellerProvider.getSellers());
	}
	
	@GetMapping("/seller/{id}")
	public ResponseEntity<SellerContract> getSellerById(@PathVariable long id) {
		SellerContract seller = sellerProvider.getSellerById(id);
		if (seller == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(seller);
	}
	
	@PostMapping("/seller")
	public ResponseEntity<SellerContract> save(@Valid @RequestBody SellerContract contract) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED).body(sellerProvider.save(contract));
	}
	
	@PutMapping("/seller/{id}")
	public ResponseEntity<SellerContract> update(@PathVariable long id, @Valid @RequestBody SellerContract contract) throws Exception {
		return ResponseEntity.ok(sellerProvider.update(id, contract));
	}
	
	@DeleteMapping("/seller/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) throws Exception {
		sellerProvider.delete(id);
		return ResponseEntity.noContent().build();
	}
}
