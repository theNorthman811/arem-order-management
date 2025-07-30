package com.arem.core.auth;
import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.arem.core.model.Seller;

public class SellerDetails implements Serializable, UserDetails
{

	private static final long serialVersionUID = 5926468583005150707L;
		
	private Seller seller;
	
	
	public SellerDetails()
	{
		this.setSeller(new Seller());
	}
	
	public SellerDetails(Seller seller)
	{
		this.setSeller(seller);
	}
	
	
	public void setUserName(String userName)
	{
		this.seller.setEmail(userName);
	}
	
	public void setPassword(String password)
	{
		this.seller.setPassword(password);
	}
	
	public void setIsAccountNonExpired(Boolean isAccountNonExpired)
	{
		this.seller.setIsAccountNonExpired(isAccountNonExpired);
	}
	
	public void setIsAccountNonLocked(Boolean isAccountNonLocked)
	{
		this.seller.setIsAccountNonLocked(isAccountNonLocked);
	}
	
	public void setIsCredentialsNonExpired(Boolean isCredentialsNonExpired)
	{
		this.seller.setIsCredentialsNonExpired(isCredentialsNonExpired);
	}
	
	public void setIsEnabled(Boolean isEnabled)
	{
		this.seller.setIsEnabled(isEnabled);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return null;
	}

	@Override
	public String getUsername()
	{
		return seller.getEmail();
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return this.seller.getIsAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return this.seller.getIsAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return this.seller.getIsCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() 
	{
		return this.seller.getIsEnabled();
	}

	@Override
	public String getPassword() 
	{
		return this.seller.getPassword();
	}

	public Seller getSeller()
	{
		return seller;
	}

	public void setSeller(Seller seller)
	{
		this.seller = seller;
	}
	
}

