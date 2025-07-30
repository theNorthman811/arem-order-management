package com.arem.core.model;

public class ProductNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(long productId) {
        super("Product not found with id: " + productId);
    }
} 