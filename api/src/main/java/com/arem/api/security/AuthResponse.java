package com.arem.api.security;

import java.io.Serializable;

import com.arem.productInput.contracts.SellerContract;
import com.arem.core.model.Customer;

public class AuthResponse implements Serializable
{
	
	private static final long serialVersionUID = -8091879091924046844L;
	
	private final String jwttoken;
	
	private SellerContract seller;
	private Customer customer;
	
	
	public AuthResponse(String jwttoken, SellerContract sellerContract)
	{
		this.jwttoken = jwttoken;
		this.seller = sellerContract;
		this.customer = null;
	}
	
	public AuthResponse(String jwttoken, Customer customer)
	{
		this.jwttoken = jwttoken;
		this.seller = null;
		this.customer = customer;
	}
	
	
	public String getToken() 
	{
		return this.jwttoken;
	}
	
	public SellerContract getSeller() 
	{
		return this.seller;
	}
	
	public Customer getCustomer() 
	{
		return this.customer;
	}
	
}