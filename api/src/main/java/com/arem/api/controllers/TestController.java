package com.arem.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.arem.api.providers.ProductProvider;
import com.arem.productInput.contracts.ProductContract;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ProductProvider productProvider;

    @GetMapping("/products")
    public ResponseEntity<List<ProductContract>> getTestProducts() {
        try {
            List<ProductContract> products = productProvider.getProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API is running!");
    }
} 