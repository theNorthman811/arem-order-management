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
import com.arem.core.model.Seller;
import com.arem.dataservice.repositories.ISellerRepository;
import com.arem.framework.runtime.Sellers;

import static org.junit.Assert.*;


public class SellerServiceTest
{
	
	@InjectMocks
	private SellerService sellerService;
	
    @Mock
	private ISellerRepository sellerRepository;
    
    @Spy
    public Sellers cache;
    
    private Seller seller1 = new Seller(1, "Samir", "Khelfane");
    private Seller seller2 = new Seller(2, "Said", "Haouche");
    private Seller seller3 = new Seller(3, "Ali", "Khelfane", "email1", "password", "032102");
    
    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testGetSellers()
	{
		Mockito.when(sellerRepository.findAll()).thenReturn(sellers());
		
		List<Seller> sellers = sellerService.getSellers();
		assertEquals(3, sellers.size());
		assertEquals(seller1, sellers.get(0));
		assertEquals(seller2, sellers.get(1));
		assertEquals(seller3, sellers.get(2));
		
		//Use Cache -- Do not use repository
		Mockito.when(sellerRepository.findAll()).thenThrow(new NullPointerException());
		Mockito.when(sellerRepository.findById(Mockito.anyLong())).thenThrow(new NullPointerException());
		assertEquals(3, sellerService.getSellers().size());
		assertEquals(seller1, sellerService.getSellerById(1));
		assertEquals(seller2, sellerService.getSellerById(2));
		assertEquals(seller3, sellerService.getSellerById(3));
		
		assertNull(sellerService.getSellerById(5));
		
		Mockito.verify(sellerRepository, Mockito.times(0)).findById(Mockito.anyLong());
		Mockito.verify(sellerRepository, Mockito.times(1)).findAll();
	}
    
    @Test
    public void testSave() throws Exception
	{    	
    	Mockito.when(sellerRepository.save(seller1)).thenReturn(seller1);
    	sellerService.save(seller1);
    	Mockito.verify(sellerRepository, Mockito.times(1)).save(seller1);
    	assertEquals(0, cache.findAll().size());
    	
    	//Activate the cache
    	Mockito.when(sellerRepository.findAll()).thenReturn(sellers());
    	sellerService.getSellers();
		
		Seller seller4 = new Seller(4, "Ali", "HAOUCHE");
		Mockito.when(sellerRepository.save(seller4)).thenReturn(seller4);
		sellerService.save(seller4);
		Mockito.verify(sellerRepository, Mockito.times(1)).save(seller4);
		
		assertEquals(4, sellerService.getSellers().size());
		assertEquals(seller1, sellerService.getSellerById(1));
		assertEquals(seller2, sellerService.getSellerById(2));
		assertEquals(seller3, sellerService.getSellerById(3));
		assertEquals(seller4, sellerService.getSellerById(4));
		
		Mockito.verify(sellerRepository, Mockito.times(0)).findById(Mockito.anyLong());
		Mockito.verify(sellerRepository, Mockito.times(1)).findAll();
		
		assertEquals(4, cache.findAll().size());
		assertTrue(cache.isEnabled());
		sellerService.disable();
		assertEquals(0, cache.findAll().size());
		assertFalse(cache.isEnabled());
	}
    
    @Test
    public void testGetSellerById()
    {
    	Mockito.when(sellerRepository.findById(1)).thenReturn(Optional.of(seller1));
    	assertEquals(seller1, sellerService.getSellerById(1));
    	
    	//Activate the cache
    	Mockito.when(sellerRepository.findAll()).thenReturn(sellers());
    	sellerService.getSellers();
		assertEquals(seller2, sellerService.getSellerById(2));
		assertEquals(seller3, sellerService.getSellerById(3));
		
		Mockito.verify(sellerRepository, Mockito.times(1)).findById(Mockito.anyLong());
		Mockito.verify(sellerRepository, Mockito.times(1)).findAll();
    }
	
	private List<Seller> sellers()
	{
		List<Seller> result = new ArrayList<Seller>();
		result.add(seller1);
		result.add(seller2);
		result.add(seller3);
		return result;
	}
}
