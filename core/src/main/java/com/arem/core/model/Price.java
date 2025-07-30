package com.arem.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity()
@Table(name = "price")
public class Price implements Serializable, ICachable {

	
	private static final long serialVersionUID = 2687255343937073934L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
	private long id;
	
	
	@Column(name = "price")
    private double price;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "measure")
    private Measure measure;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "side", length = 10)
    private Side side = Side.None;
    
	
	@Column(name = "start_date")
    private LocalDateTime startDate;
	
	
	@Column(name = "end_date")
    private LocalDateTime endDate;
    
	
	@Column(name = "creation_date")
    private LocalDateTime creationDate;
	
	
	@Column(name = "modif_date")
    private LocalDateTime modifDate;
    
	
    @ManyToOne
    private Seller modifSeller;
    
        
    @ManyToOne
    private Seller createSeller;
    
    
    @Column(name = "product_id")
	private long productId;
    
    @Transient
    private Product product;
    
    
    @Column(name = "version")
    private int version;
    
    
    public Price()
    {
    	this.startDate = LocalDateTime.now();
    	this.endDate = LocalDateTime.now().plusYears(1);
    }
    
    public Price(long id)
    {
    	this();
    	this.id = id;
    }
    
	public long getId()
	{
		return id;
	}
	
	public void setId(long id) 
	{
		this.id = id;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public void setPrice(double price)
	{
		this.price = price;
	}
	
	public Measure getMeasure()
	{
		return measure;
	}
	
	public void setMeasure(Measure measure) 
	{
		this.measure = measure;
	}
	
	public Side getSide()
	{
		return side;
	}
	
	public void setSide(Side side) 
	{
		this.side = side;
	}
	
	public LocalDateTime getStartDate()
	{
		return startDate;
	}
	
	public void setStartDate(LocalDateTime startDate)
	{
		this.startDate = startDate;
	}
	
	public LocalDateTime getEndDate()
	{
		return endDate;
	}
	
	public void setEndDate(LocalDateTime endDate)
	{
		this.endDate = endDate;
	}
	
	public LocalDateTime getCreationDate() 
	{
		return creationDate;
	}
	
	public void setCreationDate(LocalDateTime creationDate)
	{
		this.creationDate = creationDate;
	}

	public LocalDateTime getModifDate() 
	{
		return modifDate;
	}

	public void setModifDate(LocalDateTime modifDate) 
	{
		this.modifDate = modifDate;
	}

	public Seller getModifSeller() 
	{
		return modifSeller;
	}

	public void setModifSeller(Seller modifSeller) 
	{
		this.modifSeller = modifSeller;
	}

	public Seller getCreateSeller() 
	{
		return createSeller;
	}

	public void setCreateSeller(Seller creationSeller)
	{
		this.createSeller = creationSeller;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version) 
	{
		this.version = version;
	}
	
	public long getProductId()
	{
		return productId;
	}
	
	public void setProductId(long productId)
	{
		this.productId = productId;
	}
	
	public Product getProduct()
	{
		if (product == null && productId > 0)
		{
			product = new Product(productId);
		}
		return product;
	}
	
	public void setProduct(Product product)
	{
		this.product = product;
		if (product != null)
		{
			this.productId = product.getId();
		}
	}
	
	public Double getAmount()
	{
		return price;
	}
	
	public void setAmount(Double amount)
	{
		if (amount != null)
		{
			this.price = amount;
		}
	}

	@Override
	public long getGroupId() {
		return productId;
	}
}
