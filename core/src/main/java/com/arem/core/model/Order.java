package com.arem.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

	private static final long serialVersionUID = 7216330106858122873L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "customer_id", nullable = false)
	private long customerId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;
	
	@Column(name = "order_date", nullable = false)
	private LocalDateTime orderDate;
	
	@Column(name = "last_modified_date")
	private LocalDateTime lastModifiedDate;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<OrderItem> items;
	
	@Column(name = "total_amount")
	private double totalAmount;
	
	public Order() {
		this.items = new ArrayList<>();
		this.orderDate = LocalDateTime.now();
		this.lastModifiedDate = LocalDateTime.now();
		this.status = Status.PENDING;
	}
	
	public void addItem(Product product, double quantity, Measure measure, double currentPrice) {
		// Check if product already exists in the order
		OrderItem existingItem = items.stream()
				.filter(item -> item.getProduct().getId() == product.getId())
				.findFirst()
				.orElse(null);
		
		if (existingItem != null) {
			// Add quantity to existing item
			existingItem.setQuantity(existingItem.getQuantity() + quantity);
		} else {
			// Create new item
			OrderItem item = new OrderItem(product, quantity, measure, currentPrice);
			item.setOrder(this); // ✅ CORRECTION : Définir la relation
			items.add(item);
		}
		calculateTotalAmount();
	}
	
	public void removeItem(long itemId) {
		items.removeIf(item -> item.getId() == itemId);
		calculateTotalAmount();
	}
	
	private void calculateTotalAmount() {
		this.totalAmount = items.stream()
				.mapToDouble(OrderItem::getSubtotal)
				.sum();
	}

	// Getters and Setters
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
		calculateTotalAmount();
	}

	public double getTotalAmount() {
		return totalAmount;
	}
}
