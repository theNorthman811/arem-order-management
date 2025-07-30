package com.arem.core.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplier_order_item")
public class SupplierOrderItem implements Serializable, ICachable {
    
    private static final long serialVersionUID = 1L;
    public static final String CacheName = "core:supplierOrderItem";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "supplier_order_id")
    private SupplierOrder supplierOrder;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "quantity")
    private int quantity;
    
    @Column(name = "unit_price")
    private double unitPrice;
    
    @Column(name = "received_quantity")
    private int receivedQuantity = 0;
    
    @Column(name = "notes")
    private String notes;
    
    public SupplierOrderItem() {}
    
    public SupplierOrderItem(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    // Getters et Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public SupplierOrder getSupplierOrder() {
        return supplierOrder;
    }
    
    public void setSupplierOrder(SupplierOrder supplierOrder) {
        this.supplierOrder = supplierOrder;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public int getReceivedQuantity() {
        return receivedQuantity;
    }
    
    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Méthodes utilitaires
    public double getSubtotal() {
        return quantity * unitPrice;
    }
    
    public int getPendingQuantity() {
        return quantity - receivedQuantity;
    }
    
    public boolean isFullyReceived() {
        return receivedQuantity >= quantity;
    }
    
    public boolean isPartiallyReceived() {
        return receivedQuantity > 0 && receivedQuantity < quantity;
    }
    
    public boolean isNotReceived() {
        return receivedQuantity == 0;
    }
    
    public void receiveQuantity(int receivedQty) {
        if (receivedQty > 0 && receivedQty <= getPendingQuantity()) {
            this.receivedQuantity += receivedQty;
            
            // Mettre à jour le stock du produit
            if (this.product != null) {
                this.product.increaseStock(receivedQty);
            }
        }
    }
    
    @Override
    public long getGroupId() {
        return 0;
    }
} 