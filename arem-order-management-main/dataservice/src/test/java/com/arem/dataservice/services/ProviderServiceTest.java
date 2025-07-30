package com.arem.dataservice.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.arem.core.model.Provider;
import com.arem.dataservice.repositories.IProviderRepository;
import com.arem.framework.runtime.Providers;

import static org.junit.Assert.*;


public class ProviderServiceTest
{
	
	@InjectMocks
	private ProviderService providerService;
	
    @Mock
	private IProviderRepository providerRepository;
    
    @Spy
    public Providers cache;
    
    private Provider provider1 = new Provider(1, "Samir", "Khelfane");
    private Provider provider2 = new Provider(2, "Said", "Haouche");
    private Provider provider3 = new Provider(3, "Ali", "Khelfane", "email1");
    
    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testGetProviders()
	{
		Mockito.when(providerRepository.findAll()).thenReturn(providers());
		
		List<Provider> providers = providerService.getProviders();
		assertEquals(3, providers.size());
		assertEquals(provider1, providers.get(0));
		assertEquals(provider2, providers.get(1));
		assertEquals(provider3, providers.get(2));
		
		//Use Cache -- Do not use repository
		Mockito.when(providerRepository.findAll()).thenThrow(new NullPointerException());
		Mockito.when(providerRepository.findById(Mockito.anyLong())).thenThrow(new NullPointerException());
		assertEquals(3, providerService.getProviders().size());
		assertEquals(provider1, providerService.getProviderById(1));
		assertEquals(provider2, providerService.getProviderById(2));
		assertEquals(provider3, providerService.getProviderById(3));
		assertNull(providerService.getProviderById(5));
		
		Mockito.verify(providerRepository, Mockito.times(0)).findById(Mockito.anyLong());
		Mockito.verify(providerRepository, Mockito.times(1)).findAll();
	}
    
    @Test
    public void testSave() throws Exception
	{    	
    	Mockito.when(providerRepository.save(provider1)).thenReturn(provider1);
    	providerService.save(provider1);
    	Mockito.verify(providerRepository, Mockito.times(1)).save(provider1);
    	assertEquals(0, cache.findAll().size());
    	
    	//Activate the cache
    	Mockito.when(providerRepository.findAll()).thenReturn(providers());
    	providerService.getProviders();
		
		Provider provider4 = new Provider(4, "Ali", "HAOUCHE");
		Mockito.when(providerRepository.save(provider4)).thenReturn(provider4);
		providerService.save(provider4);
		Mockito.verify(providerRepository, Mockito.times(1)).save(provider4);
		
		assertEquals(4, providerService.getProviders().size());
		assertEquals(provider1, providerService.getProviderById(1));
		assertEquals(provider2, providerService.getProviderById(2));
		assertEquals(provider3, providerService.getProviderById(3));
		assertEquals(provider4, providerService.getProviderById(4));
		
		Mockito.verify(providerRepository, Mockito.times(0)).findById(Mockito.anyLong());
		Mockito.verify(providerRepository, Mockito.times(1)).findAll();
		
		assertEquals(4, cache.findAll().size());
		assertTrue(cache.isEnabled());
		providerService.disable();
		assertEquals(0, cache.findAll().size());
		assertFalse(cache.isEnabled());
	}
    
    @Test
    public void testGetProviderById()
    {
    	Mockito.when(providerRepository.findById(1)).thenReturn(Optional.of(provider1));
    	assertEquals(provider1, providerService.getProviderById(1));
    	
    	//Activate the cache
    	Mockito.when(providerRepository.findAll()).thenReturn(providers());
    	providerService.getProviders();
		assertEquals(provider2, providerService.getProviderById(2));
		assertEquals(provider3, providerService.getProviderById(3));
		
		Mockito.verify(providerRepository, Mockito.times(1)).findById(Mockito.anyLong());
		Mockito.verify(providerRepository, Mockito.times(1)).findAll();
    }
	
	private List<Provider> providers()
	{
		List<Provider> result = new ArrayList<Provider>();
		result.add(provider1);
		result.add(provider2);
		result.add(provider3);
		return result;
	}
}
