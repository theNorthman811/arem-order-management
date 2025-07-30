package com.arem.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User implements Serializable, ICachable {

	private static final long serialVersionUID = 148924116522L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
	private long id;
	
	
	@Column(name = "first_name", nullable = false)
    private String firstName;
	
	
	@Column(name = "last_name", nullable = false)
    private String lastName;
	
	
	@Column(name = "pick_name")
    private String pickName;
	
	
	@Column(name = "phone_number", nullable = false)
    private String phoneNumber;
	
	
	@Column(name = "address", nullable = false)
    private String address;
	
	
	@Column(name = "version", nullable = false)
	private int version;
	
	
	@Column(name = "modif_date")
    private LocalDateTime modifDate;
	
	
	@Column(name = "creation_date")
    private LocalDateTime creationDate;
	
	@ManyToOne
    private Seller modifSeller;
    
	@ManyToOne
    private Seller createSeller;
	
	public User()
    {
    	
    }
    
	public User(long id)
	{
		this.id = id;
	}

	public User(long id, String firstName, String lastName)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
		
	public User(long id, String firstName, String lastName, String phoneNumber)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
	}
	
    public int getVersion()
    {
		return version;
	}
    
	public void setVersion(int version) 
	{
		this.version = version;
	}
	
	public LocalDateTime getModifDate() 
	{
		return modifDate;
	}
	
	public void setModifDate(LocalDateTime modifDate)
	{
		this.modifDate = modifDate;
	}
	
	public LocalDateTime getCreationDate()
	{
		return creationDate;
	}
	
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}
	    
	public long getId() 
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public String getFirstName() 
	{
		return firstName;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String getPickName() 
	{
		return pickName;
	}
	
	public void setPickName(String pickName) 
	{
		this.pickName = pickName;
	}
	
	public String getPhoneNumber() 
	{
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) 
	{
		this.phoneNumber = phoneNumber;
	}
	
	public String getAddress() 
	{
		return address;
	}
	
	public void setAddress(String address) 
	{
		this.address = address;
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

	public void setCreateSeller(Seller createSeller) 
	{
		this.createSeller = createSeller;
	}
	
	protected void trimAll()
	{
		if (this.firstName != null)
		{
			this.firstName = this.firstName.trim();
		}
		
		if (this.lastName != null)
		{
			this.lastName = this.lastName.trim();
		}
		
		if (this.phoneNumber != null)
		{
			this.phoneNumber = this.phoneNumber.trim();
		}
		
		if (this.pickName != null)
		{
			this.pickName = this.pickName.trim();
		}
	}
	
}
