package com.arem.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;

@Entity()
@Table
(
	name = "customer",
	uniqueConstraints =
	{
		@UniqueConstraint(columnNames={"first_name", "last_name", "pick_name"}, name = "UniqueFirstNameAndLastNameAndPickName"),
		@UniqueConstraint(columnNames={"phone_number"}, name = "UniquePhoneNumber"),
		@UniqueConstraint(columnNames={"email"}, name = "UniqueEmail"),
	}
)
public class Customer extends User
{

	private static final long serialVersionUID = -5366369304341171036L;
	public static final String CacheName = "core:customer";
	
	@Column(name = "email", length = 255)
	private String email;
	
	@Column(name = "password", length = 255)
	private String password;
	
	
	public Customer()
	{
		super();
	}
	
	public Customer(long id)
	{
		super(id);
	}
	
	public Customer(long id, String firstName, String lastName)
	{
		super(id, firstName, lastName);
	}
		
	public Customer(long id, String firstName, String lastName, String phoneNumber)
	{
		super(id, firstName, lastName, phoneNumber);
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public long getGroupId()
	{
		return 0;
	}
	
	public void trimAll()
	{
		super.trimAll();
		if (email != null) {
			email = email.trim();
		}
		if (password != null) {
			password = password.trim();
		}
	}
}