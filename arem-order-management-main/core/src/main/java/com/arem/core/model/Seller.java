package com.arem.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "seller",
    uniqueConstraints = {
        @UniqueConstraint(columnNames={"first_name", "last_name"}, name = "UniqueFirstNameAndLastName"),
        @UniqueConstraint(columnNames={"email"}, name = "UniqueEmail"),
        @UniqueConstraint(columnNames={"phone_number"}, name = "UniquePhoneNumber"),
    }
)
public class Seller extends User {

	private static final long serialVersionUID = 18852967416L;
	public static final String CacheName = "core:seller";
	
	
	@Column(name = "email", nullable = false)
    private String email;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	
	@Column(name = "is_admin")
	private Boolean isAdmin;
	
		
	@Column(name = "is_accountNonExpired")
	private Boolean isAccountNonExpired;
	
	
	@Column(name = "is_accountNonLocked")
	private Boolean isAccountNonLocked;
	
	
	@Column(name = "is_credentialsNonExpired")
	private Boolean isCredentialsNonExpired;
	
	
	@Column(name = "is_enabled")
	private Boolean isEnabled;
	
	
	public Seller()
	{
		super();
	}
	
	public Seller(long id)
	{
		super(id);
	}
	
	public Seller(long id, String firstName, String lastName)
	{
		super(id, firstName, lastName);
	}
		
	public Seller(long id, String firstName, String lastName, String email, String password, String phoneNumber)
	{
		super(id, firstName, lastName, phoneNumber);
		this.password = password;
		this.email = email;
	}
	

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}	
	
	public long getGroupId()
	{
		return 0;
	}

	public Boolean getIsAdmin()
	{
		return this.isAdmin;
	}
	
	public void setIsAdmin(Boolean isAdmin)
	{
		this.isAdmin = isAdmin;
	}
	
	public Boolean getIsAccountNonExpired()
	{
		return isAccountNonExpired;
	}

	public void setIsAccountNonExpired(Boolean isAccountNonExpired)
	{
		this.isAccountNonExpired = isAccountNonExpired;
	}

	public Boolean getIsAccountNonLocked() 
	{
		return isAccountNonLocked;
	}

	public void setIsAccountNonLocked(Boolean isAccountNonLocked)
	{
		this.isAccountNonLocked = isAccountNonLocked;
	}

	public Boolean getIsCredentialsNonExpired()
	{
		return isCredentialsNonExpired;
	}

	public void setIsCredentialsNonExpired(Boolean isCredentialsNonExpired)
	{
		this.isCredentialsNonExpired = isCredentialsNonExpired;
	}

	public Boolean getIsEnabled()
	{
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public void trimAll()
	{
		super.trimAll();
		
		if (this.email != null)
		{
			this.email = this.email.trim();
		}
	}
	
}
