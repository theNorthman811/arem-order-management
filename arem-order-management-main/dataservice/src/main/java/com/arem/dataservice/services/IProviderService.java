package com.arem.dataservice.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.arem.core.model.Provider;

@Service
public interface IProviderService
{

	public List<Provider> getProviders();
	
	public Provider getProviderById(long id);
	
	public Provider getProviderByFirstNameAndLastNameAndPickName(String firstName, String lastName, String pickName);
	
	public Provider getProviderByPickName(String pickName);
	
	public Provider getProviderByPhoneNumber(String phone);
	
	public long save(Provider provider) throws Exception;
	
	public void delete(Provider provider) throws Exception;
	
	public void disable();
	
}
