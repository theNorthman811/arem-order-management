package com.arem.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Facture implements Serializable {

	private static final long serialVersionUID = -2406610034767981585L;
	
	private long id;
    private long idCustomer;
    private String comment;
    
    private Status status;
    
    private List<Order> orders;
    private List<Payment> payments;
    
    private int version;
    private LocalDateTime modifDate;
    private long modifSellerId;
    
    private LocalDateTime creationDate;
    private long createSellerId;
    
    private Side side;

    public Facture()
    {
        this.payments = new ArrayList<Payment>();
        this.orders =  new ArrayList<Order>();
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdCustomer() {
		return idCustomer;
	}

	public void setIdCustomer(long idCustomer) {
		this.idCustomer = idCustomer;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public LocalDateTime getModifDate() {
		return modifDate;
	}

	public void setModifDate(LocalDateTime modifDate) {
		this.modifDate = modifDate;
	}

	public long getModifSellerId() {
		return modifSellerId;
	}

	public void setModifSellerId(long modifSellerId) {
		this.modifSellerId = modifSellerId;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public long getCreateSellerId() {
		return createSellerId;
	}

	public void setCreateSellerId(long createSellerId) {
		this.createSellerId = createSellerId;
	}

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}
    
    
}
