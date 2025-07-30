package com.arem.productInput.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.arem.productInput.contracts.SellerContract;

public class SellerRuleTest
{
	
	@InjectMocks
	private SellerRule rule;
	
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
		SellerContract contract = new SellerContract();
		List<String> errors = new ArrayList<String>();
		
		assertFalse(rule.validate(contract, errors));
		assertEquals(5, errors.size());
		assertEquals("first name could not be null", errors.get(0));
		
		errors.clear();
		contract.setFirstName("RA");
		assertFalse(rule.validate(contract, errors));
		assertEquals(5, errors.size());
		assertEquals("given first name is not valid", errors.get(0));
		
		errors.clear();
		contract.setFirstName("Ramdane");
		assertFalse(rule.validate(contract, errors));
		assertEquals(4, errors.size());
		assertEquals("address could not be null", errors.get(0));
		
		errors.clear();
		contract.setAddress("BERK");
		assertFalse(rule.validate(contract, errors));
		assertEquals(4, errors.size());
		assertEquals("given address is not valid", errors.get(0));
		
		errors.clear();
		contract.setAddress("BERKOUKA, MAATKAS, 15151");
		assertFalse(rule.validate(contract, errors));
		assertEquals(3, errors.size());
		assertEquals("last name could not be null", errors.get(0));
		
		errors.clear();
		contract.setLastName("HA");
		assertFalse(rule.validate(contract, errors));
		assertEquals(3, errors.size());
		assertEquals("given last name is not valid", errors.get(0));
		
		errors.clear();
		contract.setLastName("HAOUCHE");
		assertFalse(rule.validate(contract, errors));
		assertEquals(2, errors.size());
		assertEquals("password could not be null", errors.get(0));
		
	}


}
