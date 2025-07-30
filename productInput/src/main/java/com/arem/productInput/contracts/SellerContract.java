package com.arem.productInput.contracts;

import com.arem.core.model.Seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SellerContract extends UserContract<Seller>
{
		
	public SellerContract(Seller seller)
	{
		super(seller);
	}
	
	public SellerContract()
	{
		super(new Seller());
	}

	@Override
	@NotBlank(message = "Le prénom est obligatoire")
	@Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
	public String getFirstName() {
		return super.getFirstName();
	}

	@Override
	@NotBlank(message = "Le nom est obligatoire")
	@Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
	public String getLastName() {
		return super.getLastName();
	}

	@Override
	@Size(max = 50, message = "Le surnom ne peut pas dépasser 50 caractères")
	public String getPickName() {
		return super.getPickName();
	}

	@Override
	@NotBlank(message = "Le numéro de téléphone est obligatoire")
	@Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "Le numéro de téléphone doit contenir entre 8 et 15 chiffres, avec un + optionnel au début")
	public String getPhoneNumber() {
		return super.getPhoneNumber();
	}

	@Override
	@Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
	public String getAddress() {
		return super.getAddress();
	}

	@NotBlank(message = "Le mot de passe est obligatoire")
	@Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
	public String getPassword()
	{
		return null;
	}

	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "L'email doit être valide")
	public String getEmail()
	{
		return this.model.getEmail();
	}
	
	public void setEmail(String email)
	{
		this.model.setEmail(email);
	}
		
	public Boolean getIsAdmin()
	{
		return this.model.getIsAdmin();
	}
	
	public void setIsAdmin(Boolean isAdmin)
	{
		this.model.setIsAdmin(isAdmin);
	}
	
	public Boolean getIsAccountNonExpired()
	{
		return this.model.getIsAccountNonExpired();
	}

	public void setIsAccountNonExpired(Boolean isAccountNonExpired)
	{
		this.model.setIsAccountNonExpired(isAccountNonExpired);
	}

	public Boolean getIsAccountNonLocked() 
	{
		return this.model.getIsAccountNonLocked();
	}

	public void setIsAccountNonLocked(Boolean isAccountNonLocked)
	{
		this.model.setIsAccountNonLocked(isAccountNonLocked);
	}

	public Boolean getIsCredentialsNonExpired()
	{
		return this.model.getIsCredentialsNonExpired();
	}

	public void setIsCredentialsNonExpired(Boolean isCredentialsNonExpired)
	{
		this.model.setIsCredentialsNonExpired(isCredentialsNonExpired);
	}

	public Boolean getIsEnabled()
	{
		return this.model.getIsEnabled();
	}

	public void setIsEnabled(Boolean isEnabled)
	{
		this.model.setIsEnabled(isEnabled);
	}
}
