package com.arem.api.providers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arem.core.model.Provider;
import com.arem.core.model.Seller;
import com.arem.dataservice.services.IProviderService;
import com.arem.productInput.contracts.ProviderContract;
import com.arem.productInput.roles.UserRole;
import com.arem.productInput.rules.ProviderRule;


@Component
public class UserProvider 
{
	
	@Autowired
	private IProviderService providerService;
	
	@Autowired
	private AuthProvider authProvider;
	
	@Autowired
	private ProviderRule rule;
	
	public List<ProviderContract> getProviders()
	{
		if (UserRole.CanReadProviders(authProvider.getCurrentUser()))
		{
			Iterable<Provider> all = providerService.getProviders();
			List<ProviderContract> result = new ArrayList<ProviderContract>();
			for(Provider Provider : all)
			{
				ProviderContract contract = new ProviderContract(Provider);
				result.add(contract);
			}
			return result;
		}
		else
		{
			throw new IllegalArgumentException("You have not rights to read providers");
		}		
	}
	
	public ProviderContract getProviderById(long id)
	{
		if (UserRole.CanReadProviders(authProvider.getCurrentUser()))
		{
			Provider Provider = providerService.getProviderById(id);
			if (Provider == null)
			{
				return null;
			}
			return new ProviderContract(Provider);
		}
		else
		{
			throw new IllegalArgumentException("You have not rights to read providers");
		}
	}
	
	public long save(ProviderContract contract) throws Exception
	{
		if (contract == null)
		{
		   throw new IllegalArgumentException("Provider could not be null");	
		}
		
		Seller currentUser = authProvider.getCurrentUser();
		
		contract.getModel().trimAll();
		
		if (UserRole.CanUpdateProviders(currentUser))
		{
			List<String> errors = new ArrayList<String>();
			if (rule.validate(contract, errors))
			{
				Provider provider = contract.getModel();
				provider.setModifDate(LocalDateTime.now());
				provider.setModifSeller(currentUser);
				
				if (provider.getId() == 0)
				{
					provider.setCreationDate(LocalDateTime.now());
					provider.setCreateSeller(currentUser);
					provider.setVersion(1);
				}
				else
				{
					Provider oldProvider = providerService.getProviderById(provider.getId());
					if (oldProvider == null)
					{
						throw new IllegalArgumentException(String.format("provider with id %s is not found", provider.getId()));
					}
					
					int version = oldProvider.getVersion() + 1;
					provider.setVersion(version);
					provider.setCreateSeller(oldProvider.getCreateSeller());
					provider.setCreationDate(oldProvider.getCreationDate());
				}
				
				if (this.chekProvider(provider, errors))
				{
					return providerService.save(provider);
				}
				else
				{
					throw new IllegalArgumentException(String.join(";", errors));
				}
			}			
			else
			{
				throw new IllegalArgumentException(String.join(";", errors));
			}
		}
		else
		{
			throw new IllegalArgumentException("You have not rights to save or update provider");
		}
	}
	
	private Boolean chekProvider(Provider provider, List<String> errors)
	{
		Provider oldProvider = providerService.getProviderByPhoneNumber(provider.getPhoneNumber());
		if (oldProvider != null && oldProvider.getId() != provider.getId())
		{
			errors.add("Another provider with same phone number already exists");
		}
		
		oldProvider = providerService.getProviderByPickName(provider.getPickName());
		if (oldProvider != null && oldProvider.getId() != provider.getId())
		{
			errors.add("Another provider with same pick name already exists");
		}
		
		return errors.isEmpty();
	}

}
