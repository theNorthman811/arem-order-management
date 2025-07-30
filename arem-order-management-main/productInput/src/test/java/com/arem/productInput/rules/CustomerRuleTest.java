package com.arem.productInput.rules;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.arem.core.model.Seller;
import com.arem.dataservice.services.ISellerService;
import com.arem.productInput.contracts.CustomerContract;

public class CustomerRuleTest
{
	
	@InjectMocks
	private CustomerRule rule;
	
	@Mock
	private ISellerService sellerService;
	
	@Before
    public void setUp()
    { 
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void testValidate_NullContract()
	{
		List<String> errors = new ArrayList<String>();
		try
		{
			rule.validate(null, errors);
			fail("Exception should be thrown");
		}
		catch(IllegalArgumentException ex)
		{
			assertEquals("contract cannot be null", ex.getMessage());
		}
	}
	
	@Test
	public void testValidate()
	{
		CustomerContract contract = new CustomerContract();
		List<String> errors = new ArrayList<String>();
		Mockito.when(sellerService.getSellerById(1)).thenReturn(new Seller(1));
		
		assertFalse(rule.validate(contract, errors));
		assertEquals(4, errors.size());
		assertEquals("first name could not be null", errors.get(0));
		
		errors.clear();
		contract.setFirstName("RA");
		assertFalse(rule.validate(contract, errors));
		assertEquals(4, errors.size());
		assertEquals("given first name is not valid", errors.get(0));
		
		errors.clear();
		contract.setFirstName("Ramdane");
		assertFalse(rule.validate(contract, errors));
		assertEquals(3, errors.size());
		assertEquals("address could not be null", errors.get(0));
		
		errors.clear();
		contract.setAddress("BERK");
		assertFalse(rule.validate(contract, errors));
		assertEquals(3, errors.size());
		assertEquals("given address is not valid", errors.get(0));
		
		errors.clear();
		contract.setAddress("BERKOUKA, MAATKAS, 15151");
		assertFalse(rule.validate(contract, errors));
		assertEquals(2, errors.size());
		assertEquals("last name could not be null", errors.get(0));
		
		errors.clear();
		contract.setLastName("HA");
		assertFalse(rule.validate(contract, errors));
		assertEquals(2, errors.size());
		assertEquals("given last name is not valid", errors.get(0));
		
		errors.clear();
		contract.setLastName("HAOUCHE");
		assertFalse(rule.validate(contract, errors));
		assertEquals(1, errors.size());
		assertEquals("modif seller is not filled", errors.get(0));
		
	}

}







