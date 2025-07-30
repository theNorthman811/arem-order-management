package com.arem.productInput.contracts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.arem.core.model.Cart;

public class CartContract {
    private long id;
    private long customerId;
    private List<CartItemContract> items;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private boolean isActive;
    private double total;
    private int itemCount;
    
    public CartContract() {
        this.items = new ArrayList<>();
    }
    
    public CartContract(Cart cart) {
        this();
        this.id = cart.getId();
        this.customerId = cart.getCustomer() != null ? cart.getCustomer().getId() : 0;
        this.creationDate = cart.getCreationDate();
        this.modificationDate = cart.getModificationDate();
        this.isActive = cart.isActive();
        this.total = cart.getTotal();
        this.itemCount = cart.getItemCount();
        
        if (cart.getItems() != null) {
            this.items = cart.getItems().stream()
                    .map(CartItemContract::new)
                    .collect(Collectors.toList());
        }
    }
    
    public Cart getModel() {
        Cart cart = new Cart();
        cart.setId(this.id);
        cart.setCreationDate(this.creationDate);
        cart.setModificationDate(this.modificationDate);
        cart.setActive(this.isActive);
        return cart;
    }
    
    // Getters et Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }
    
    public List<CartItemContract> getItems() {
        return items;
    }
    
    public void setItems(List<CartItemContract> items) {
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
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public int getItemCount() {
        return itemCount;
    }
    
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
} 