package com.arem.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplier_order")
public class SupplierOrder implements Serializable, ICachable {
    
    private static final long serialVersionUID = 1L;
    public static final String CacheName = "core:supplierOrder";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    
    @Column(name = "order_number", unique = true)
    private String orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;
    
    @OneToMany(mappedBy = "supplierOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<SupplierOrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.CREATED;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;
    
    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;
    
    @Column(name = "total_amount")
    private double totalAmount;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    
    @Column(name = "modification_date")
    private LocalDateTime modificationDate;
    
    public SupplierOrder() {
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
        this.orderDate = LocalDateTime.now();
        this.orderNumber = generateOrderNumber();
    }
    
    public SupplierOrder(Provider provider, Seller seller) {
        this();
        this.provider = provider;
        this.seller = seller;
    }
    
    // Getters et Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public Seller getSeller() {
        return seller;
    }
    
    public void setSeller(Seller seller) {
        this.seller = seller;
    }
    
    public List<SupplierOrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<SupplierOrderItem> items) {
        this.items = items;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
        this.modificationDate = LocalDateTime.now();
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDateTime getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    
    public void setExpectedDeliveryDate(LocalDateTime expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    
    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }
    
    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    // Méthodes utilitaires
    public void addItem(SupplierOrderItem item) {
        items.add(item);
        item.setSupplierOrder(this);
        updateTotal();
    }
    
    public void removeItem(SupplierOrderItem item) {
        items.remove(item);
        item.setSupplierOrder(null);
        updateTotal();
    }
    
    public void updateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
    }
    
    public double getTotal() {
        return totalAmount;
    }
    
    public int getItemCount() {
        return items.stream()
                .mapToInt(SupplierOrderItem::getQuantity)
                .sum();
    }
    
    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }
    
    public boolean isPending() {
        return status == Status.CREATED;
    }
    
    public boolean isInProgress() {
        return status == Status.CREATED;
    }
    
    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }
    
    private String generateOrderNumber() {
        return "SO-" + System.currentTimeMillis();
    }
    
    @Override
    public long getGroupId() {
        return 0;
    }
} 