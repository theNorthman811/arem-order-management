package com.arem.dataservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.arem.core.model.Provider;
import com.arem.dataservice.repositories.IProviderRepository;
import com.arem.framework.StringHelper;
import com.arem.framework.runtime.Providers;

@Component
public class ProviderService implements IProviderService
{
	
	@Autowired
	private IProviderRepository providerRepository;
	
	@Autowired
	private Providers cache;

	private final Object sync = new Object();

	private void enableCache()
	{
		if (!cache.isEnabled())
		{
			synchronized(sync)
			{
				if (!cache.isEnabled())
				{
					Iterable<Provider> providers = providerRepository.findAll();
					cache.putAll(providers);
				}
			}
		}
	}
	
	@Override
	public List<Provider> getProviders()
	{
		enableCache();
		return cache.findAll();
	}

	@Override
	public Provider getProviderById(long id)
	{
		enableCache();
		return cache.findById(id);
	}
	

	@Override
	public Provider getProviderByPickName(String pickName)
	{
		enableCache();
		if (StringHelper.isNullOrEmpty(pickName))
		{
			return null;
		}
		return cache.findOne(p -> StringHelper.isNotNullOrEmpty(p.getPickName()) && p.getPickName().trim().toUpperCase().equals(pickName.trim().toUpperCase()));
	}
	
	@Override
	public Provider getProviderByFirstNameAndLastNameAndPickName(String firstName, String lastName, String pickName)
	{
		enableCache();
		
		if (StringHelper.isNullOrEmpty(firstName) || StringHelper.isNullOrEmpty(lastName))
		{
			return null;
		}
		
		if (StringHelper.isNullOrEmpty(pickName))
		{
			return cache.findOne(p -> p.getFirstName().trim().toUpperCase().equals(firstName.trim().toUpperCase()) &&
					p.getLastName().trim().toUpperCase().equals(lastName.trim().toUpperCase()) && StringHelper.isNullOrEmpty(p.getPickName()));
		}
		
		else
		{
			return cache.findOne(p -> p.getFirstName().trim().toUpperCase().equals(firstName.trim().toUpperCase()) &&
					p.getLastName().trim().toUpperCase().equals(lastName.trim().toUpperCase()) && StringHelper.isNotNullOrEmpty(p.getPickName())
					&& p.getPickName().trim().toUpperCase().equals(pickName.trim().toUpperCase()));
		}
	}
	
	@Override
	public Provider getProviderByPhoneNumber(String phoneNumber)
	{
		enableCache();
		if (StringHelper.isNullOrEmpty(phoneNumber))
		{
			return null;
		}
		return cache.findOne(p -> p.getPhoneNumber().trim().equals(phoneNumber.trim()));
	}
	
	@Override
	public long save(Provider provider) throws Exception
	{
		providerRepository.save(provider);
		if (cache.isEnabled())
		{
			try
			{
				cache.save(provider);
			}
			catch(Exception ex)
			{
				disable();
			}
		}
		return provider.getId();
	}
	
	@Override
	public void delete(Provider provider) throws Exception {
		providerRepository.delete(provider);
		if (cache.isEnabled()) {
			try {
				cache.delete(provider);
			} catch(Exception ex) {
				disable();
			}
		}
	}

	@Override
	public void disable()
	{
		cache.disable();
	}
}
