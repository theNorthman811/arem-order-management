package com.arem.api.controllers;

import java.util.List;
import java.util.ArrayList;

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

import com.arem.api.providers.ProductProvider;
import com.arem.productInput.contracts.ProductContract;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class ProductController 
{
	@Autowired
	private ProductProvider productProvider;
		
	@GetMapping("/products")
	public ResponseEntity<List<ProductContract>> getProducts() {
		return ResponseEntity.ok(productProvider.getProducts());
	}
	
	@GetMapping("/products/public")
	public ResponseEntity<List<ProductContract>> getPublicProducts() {
		try {
			return ResponseEntity.ok(productProvider.getPublicProducts());
		} catch (Exception e) {
			// Log l'erreur et retourner une liste vide en cas d'erreur
			System.err.println("Erreur dans getPublicProducts: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	@GetMapping("/product/{id}")
	public ResponseEntity<ProductContract> getProductById(@PathVariable long id) {
		return ResponseEntity.ok(productProvider.getProductById(id));
	}
	
	@PostMapping("/product")
	public ResponseEntity<ProductContract> save(@Valid @RequestBody ProductContract contract) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED).body(productProvider.save(contract));
	}
	
	@PutMapping("/product/{id}")
	public ResponseEntity<ProductContract> update(@PathVariable long id, @Valid @RequestBody ProductContract contract) throws Exception {
		return ResponseEntity.ok(productProvider.update(id, contract));
	}
	
	@DeleteMapping("/product/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) throws Exception {
		productProvider.delete(id);
		return ResponseEntity.noContent().build();
	}
}
