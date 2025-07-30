package com.arem.api.providers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arem.core.model.Customer;
import com.arem.core.model.Seller;
import com.arem.dataservice.services.ICustomerService;
import com.arem.productInput.contracts.CustomerContract;
import com.arem.productInput.roles.UserRole;
import com.arem.productInput.rules.CustomerRule;

@Component
public class CustomerProvider
{

	@Autowired
	private ICustomerService customerService;
	
	@Autowired
	private CustomerRule customerRule;
	
	@Autowired
	private AuthProvider authProvider;
	
	public List<CustomerContract> getCustomers()
	{
		List<Customer> all = customerService.getCustomers();
		List<CustomerContract> result = new ArrayList<>();
		for(Customer customer : all)
		{
			result.add(new CustomerContract(customer));
		}
		return result;
	}
	
	public CustomerContract getCustomerById(long id)
	{
		Customer customer = customerService.getCustomerById(id);
		if (customer == null)
		{
			return null;
		}
		return new CustomerContract(customer);
	}
	
	public CustomerContract save(CustomerContract contract) throws Exception
	{
		if (contract == null)
		{
			throw new IllegalArgumentException("customer could not be null");
		}
		
		Seller currentUser = authProvider.getCurrentUser();
		List<String> errors = new ArrayList<>();
		
		if (UserRole.CanUpdateCustomers(currentUser))
		{
			if (customerRule.validate(contract, errors))
			{
				Customer customer = contract.getModel();
				
				customer.setModifDate(LocalDateTime.now());
				customer.setModifSeller(currentUser);
				
				if (customer.getId() == 0)
				{
					customer.setCreationDate(LocalDateTime.now());
					customer.setCreateSeller(currentUser);
					customer.setVersion(1);
					if (check(customer, errors))
					{
						long id = customerService.save(customer);
						customer.setId(id);
						return new CustomerContract(customer);
					}
					else
					{
						throw new IllegalArgumentException(String.join(";", errors));
					}
				}
				else
				{
					Customer oldCustomer = customerService.getCustomerById(customer.getId());
					if (oldCustomer == null)
					{
						throw new IllegalArgumentException("the customer id specified is not valid : " + customer.getId());
					}
					customer.setCreationDate(oldCustomer.getCreationDate());
					customer.setCreateSeller(oldCustomer.getCreateSeller());
					int version = oldCustomer.getVersion() + 1;
					customer.setVersion(version);
					
					if (check(customer, errors))
					{
						long id = customerService.save(customer);
						customer.setId(id);
						return new CustomerContract(customer);
					}
					else
					{
						throw new IllegalArgumentException(String.join(";", errors));
					}
				}
			}
			else
			{
				throw new IllegalArgumentException(String.join(";", errors));
			}
		}
		else
		{
			throw new IllegalArgumentException("You are not authorized to save or update customers");
		}
	}
	
	public CustomerContract update(long id, CustomerContract contract) throws Exception
	{
		if (contract == null)
		{
			throw new IllegalArgumentException("customer could not be null");
		}
		
		Customer existingCustomer = customerService.getCustomerById(id);
		if (existingCustomer == null)
		{
			throw new IllegalArgumentException("Customer not found with id: " + id);
		}
		
		contract.setId(id);
		return save(contract);
	}
	
	public void delete(long id) throws Exception
	{
		Seller currentUser = authProvider.getCurrentUser();
		if (UserRole.CanUpdateCustomers(currentUser))
		{
			Customer customer = customerService.getCustomerById(id);
			if (customer != null)
			{
				customerService.delete(customer);
			}
			else
			{
				throw new IllegalArgumentException("Customer not found with id: " + id);
			}
		}
		else
		{
			throw new IllegalArgumentException("You are not authorized to delete customers");
		}
	}
	
	private Boolean check(Customer customer, List<String> errors)
	{
		Customer oldCustomer = customerService.getCustomerByPhoneNumber(customer.getPhoneNumber());
		if (oldCustomer != null && oldCustomer.getId() != customer.getId())
		{
			errors.add("Another customer with same phone number already exists");
		}
		// Ajoute ici une vérification sur l'email si tu as un champ email pour Customer
		return errors.isEmpty();
	}
}






