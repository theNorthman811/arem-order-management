package com.arem.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart")
public class Cart implements Serializable, ICachable {
    
    private static final long serialVersionUID = 1L;
    public static final String CacheName = "core:cart";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    
    @Column(name = "modification_date")
    private LocalDateTime modificationDate;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    public Cart() {
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
    }
    
    public Cart(Customer customer) {
        this();
        this.customer = customer;
    }
    
    // Getters et Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }
    
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    // Méthodes utilitaires
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        this.modificationDate = LocalDateTime.now();
    }
    
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        this.modificationDate = LocalDateTime.now();
    }
    
    public void clear() {
        items.clear();
        this.modificationDate = LocalDateTime.now();
    }
    
    public double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
    }
    
    public int getItemCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    @Override
    public long getGroupId() {
        return 0;
    }
} 