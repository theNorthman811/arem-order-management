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

import com.arem.api.providers.PriceProvider;
import com.arem.productInput.contracts.PriceContract;
import com.arem.dataservice.services.IPriceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class PriceController {
	
	@Autowired
	private PriceProvider priceProvider;
	
	@Autowired
	private IPriceService priceService;
		
	@GetMapping("/prices")
	public ResponseEntity<List<PriceContract>> getPrices() {
		return ResponseEntity.ok(priceProvider.getPrices());
	}
	
	@GetMapping("/price/{id}")
	public ResponseEntity<PriceContract> getPriceById(@PathVariable long id) {
		PriceContract price = priceProvider.getPriceById(id);
		if (price == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(price);
	}
	
	@PostMapping("/price")
	public ResponseEntity<PriceContract> save(@Valid @RequestBody PriceContract contract) throws Exception {
		return ResponseEntity.status(HttpStatus.CREATED).body(priceProvider.save(contract));
	}
	
	@PutMapping("/price/{id}")
	public ResponseEntity<PriceContract> update(@PathVariable long id, @Valid @RequestBody PriceContract contract) throws Exception {
		return ResponseEntity.ok(priceProvider.update(id, contract));
	}
	
	@DeleteMapping("/price/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id) throws Exception {
		priceProvider.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/prices/clear-cache")
	public ResponseEntity<Void> clearCache() {
		priceService.disable();
		return ResponseEntity.ok().build();
	}
}
