package com.arem.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "provider",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"first_name", "last_name", "pick_name"}, name = "UniqueFirstNameAndLastNameAndPickName"),
		@UniqueConstraint(columnNames={"phone_number"}, name = "UniquePhoneNumber"),
		@UniqueConstraint(columnNames={"email"}, name = "UniqueEmail")
	}
)
public class Provider extends User implements Serializable, ICachable {

	private static final long serialVersionUID = 1859745615554L;
	public static final String CacheName = "core:provider";
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "tax_number")
	private String taxNumber;
	
	@Column(name = "payment_terms")
	private String paymentTerms;
	
	@Column(name = "is_active")
	private boolean isActive = true;
	
	@Column(name = "rating")
	private Double rating;
	
	@Column(name = "notes")
	private String notes;
	
	@OneToMany(mappedBy = "provider")
	private List<SupplierOrder> orders = new ArrayList<>();
	
	public Provider() {
		super();
	}
	
	public Provider(long id) {
		super(id);
	}
	
	public Provider(long id, String firstName, String lastName) {
		super(id, firstName, lastName);
	}
		
	public Provider(long id, String firstName, String lastName, String phoneNumber) {
		super(id, firstName, lastName, phoneNumber);
	}
	
	public Provider(String firstName, String lastName, String phoneNumber, String email, String companyName) {
		super();
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setPhoneNumber(phoneNumber);
		this.email = email;
		this.companyName = companyName;
	}

	// Getters et Setters
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<SupplierOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<SupplierOrder> orders) {
		this.orders = orders;
	}

	// Méthodes utilitaires
	public void addOrder(SupplierOrder order) {
		orders.add(order);
		order.setProvider(this);
	}
	
	public void removeOrder(SupplierOrder order) {
		orders.remove(order);
		order.setProvider(null);
	}
	
	public int getTotalOrders() {
		return orders.size();
	}
	
	public double getTotalSpent() {
		return orders.stream()
				.filter(order -> order.getStatus() == Status.COMPLETED)
				.mapToDouble(SupplierOrder::getTotal)
				.sum();
	}

	@Override
	public long getGroupId() {
		return 0;
	}
	
	@Override
	public void trimAll() {
		super.trimAll();
		if (email != null) email = email.trim();
		if (companyName != null) companyName = companyName.trim();
		if (taxNumber != null) taxNumber = taxNumber.trim();
		if (paymentTerms != null) paymentTerms = paymentTerms.trim();
		if (notes != null) notes = notes.trim();
	}
}
