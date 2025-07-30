package com.arem.core.model;

import java.io.Serializable;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private double quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Measure measure;
    
    @Column(name = "unit_price", nullable = false)
    private double unitPrice;
    
    @Column(nullable = false)
    private double subtotal;
    
    public OrderItem() {
    }
    
    public OrderItem(Product product, double quantity, Measure measure, double currentPrice) {
        this.product = product;
        this.quantity = quantity;
        this.measure = measure;
        this.unitPrice = currentPrice;
        calculateSubtotal();
    }
    
    private void calculateSubtotal() {
        this.subtotal = this.quantity * this.unitPrice;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product, double currentPrice) {
        this.product = product;
        this.unitPrice = currentPrice;
        calculateSubtotal();
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }
    
    public Measure getMeasure() {
        return measure;
    }
    
    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
} 