package com.arem.productInput.contracts;
import com.arem.core.model.Customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerContract extends UserContract<Customer>
{

    public CustomerContract(Customer customer)
	{
    	super(customer);
	}
	
	public CustomerContract()
	{
		super(new Customer());
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
}
