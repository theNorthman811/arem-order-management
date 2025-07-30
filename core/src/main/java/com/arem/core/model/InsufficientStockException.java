package com.arem.core.model;

public class InsufficientStockException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final long productId;
    private final double requestedQuantity;
    private final double availableQuantity;

    public InsufficientStockException(long productId, double requestedQuantity, double availableQuantity) {
        super(String.format("Stock insuffisant pour le produit %d : demandé %.2f, disponible %.2f", 
            productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public long getProductId() {
        return productId;
    }

    public double getRequestedQuantity() {
        return requestedQuantity;
    }

    public double getAvailableQuantity() {
        return availableQuantity;
    }
} 