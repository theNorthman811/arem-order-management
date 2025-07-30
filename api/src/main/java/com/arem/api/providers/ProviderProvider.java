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
public class ProviderProvider {
	
	@Autowired
	private IProviderService providerService;
	
	@Autowired
	private ProviderRule providerRule;
	
	@Autowired
	private AuthProvider authProvider;
	
	public List<ProviderContract> getProviders() {
		List<Provider> all = providerService.getProviders();
		List<ProviderContract> result = new ArrayList<>();
		for(Provider provider : all) {
			result.add(new ProviderContract(provider));
		}
		return result;
	}
	
	public ProviderContract getProviderById(long id) {
		Provider provider = providerService.getProviderById(id);
		if (provider == null) {
			return null;
		}
		return new ProviderContract(provider);
	}
	
	public ProviderContract save(ProviderContract contract) throws Exception {
		if (contract == null) {
			throw new IllegalArgumentException("provider could not be null");
		}
		
		Seller currentUser = authProvider.getCurrentUser();
		List<String> errors = new ArrayList<>();
		
		if (UserRole.CanUpdateProviders(currentUser)) {
			if (providerRule.validate(contract, errors)) {
				Provider provider = contract.getModel();
				
				provider.setModifDate(LocalDateTime.now());
				provider.setModifSeller(currentUser);
				
				if (provider.getId() == 0) {
					provider.setCreationDate(LocalDateTime.now());
					provider.setCreateSeller(currentUser);
					provider.setVersion(1);
					if (check(provider, errors)) {
						long id = providerService.save(provider);
						provider.setId(id);
						return new ProviderContract(provider);
					} else {
						throw new IllegalArgumentException(String.join(";", errors));
					}
				} else {
					Provider oldProvider = providerService.getProviderById(provider.getId());
					if (oldProvider == null) {
						throw new IllegalArgumentException("the provider id specified is not valid : " + provider.getId());
					}
					provider.setCreationDate(oldProvider.getCreationDate());
					provider.setCreateSeller(oldProvider.getCreateSeller());
					int version = oldProvider.getVersion() + 1;
					provider.setVersion(version);
					
					if (check(provider, errors)) {
						long id = providerService.save(provider);
						provider.setId(id);
						return new ProviderContract(provider);
					} else {
						throw new IllegalArgumentException(String.join(";", errors));
					}
				}
			} else {
				throw new IllegalArgumentException(String.join(";", errors));
			}
		} else {
			throw new IllegalArgumentException("You are not authorized to save or update providers");
		}
	}
	
	public ProviderContract update(long id, ProviderContract contract) throws Exception {
		if (contract == null) {
			throw new IllegalArgumentException("provider could not be null");
		}
		
		Provider existingProvider = providerService.getProviderById(id);
		if (existingProvider == null) {
			throw new IllegalArgumentException("Provider not found with id: " + id);
		}
		
		contract.setId(id);
		return save(contract);
	}
	
	public void delete(long id) throws Exception {
		Seller currentUser = authProvider.getCurrentUser();
		if (UserRole.CanUpdateProviders(currentUser)) {
			Provider provider = providerService.getProviderById(id);
			if (provider != null) {
				providerService.delete(provider);
			} else {
				throw new IllegalArgumentException("Provider not found with id: " + id);
			}
		} else {
			throw new IllegalArgumentException("You are not authorized to delete providers");
		}
	}
	
	private Boolean check(Provider provider, List<String> errors) {
		Provider oldProvider = providerService.getProviderByPhoneNumber(provider.getPhoneNumber());
		if (oldProvider != null && oldProvider.getId() != provider.getId()) {
			errors.add("Another provider with same phone number already exists");
		}
		// Ajoute ici une vérification sur l'email si tu as un champ email pour Provider
		return errors.isEmpty();
	}
} 