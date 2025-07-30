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
import com.arem.core.model.Customer;
import com.arem.dataservice.repositories.ICustomerRepository;
import com.arem.framework.runtime.Customers;

import static org.junit.Assert.*;


public class CustomerServiceTest
{
	
	@InjectMocks
	private CustomerService customerService;
	
    @Mock
	private ICustomerRepository customerRepository;
    
    @Spy
    public Customers cache;
    
    private Customer customer1 = new Customer(1, "Samir", "Khelfane");
    private Customer customer2 = new Customer(2, "Said", "Haouche");
    private Customer customer3 = new Customer(3, "Ali", "Khelfane", "email1");
    
    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testGetCustomers()
	{
		Mockito.when(customerRepository.findAll()).thenReturn(customers());
		List<Customer> customers = customerService.getCustomers();
		assertEquals(3, customers.size());
		assertEquals(customer1, customers.get(0));
		assertEquals(customer2, customers.get(1));
		assertEquals(customer3, customers.get(2));
				
		//Use Cache -- Do not use repository
		Mockito.when(customerRepository.findAll()).thenThrow(new NullPointerException());
		Mockito.when(customerRepository.findById(Mockito.anyLong())).thenThrow(new NullPointerException());
		assertEquals(3, customerService.getCustomers().size());
		assertEquals(customer1, customerService.getCustomerById(1));
		assertEquals(customer2, customerService.getCustomerById(2));
		assertEquals(customer3, customerService.getCustomerById(3));
		assertNull(customerService.getCustomerById(5));
		Mockito.verify(customerRepository, Mockito.times(0)).findById(Mockito.anyLong());
		Mockito.verify(customerRepository, Mockito.times(1)).findAll();
	}
    
    @Test
    public void testSave() throws Exception
	{    	
    	Mockito.when(customerRepository.save(customer1)).thenReturn(customer1);
    	customerService.save(customer1);    	
    	Mockito.verify(customerRepository, Mockito.times(1)).save(customer1);
    	assertEquals(0, cache.findAll().size());
    	
    	//Activate the cache
    	Mockito.when(customerRepository.findAll()).thenReturn(customers());
    	customerService.getCustomers();
		
		Customer customer4 = new Customer(4, "Ali", "HAOUCHE");
		Mockito.when(customerRepository.save(customer4)).thenReturn(customer4);
		customerService.save(customer4);
		Mockito.verify(customerRepository, Mockito.times(1)).save(customer4);
		
		assertEquals(4, customerService.getCustomers().size());
		assertEquals(customer1, customerService.getCustomerById(1));
		assertEquals(customer2, customerService.getCustomerById(2));
		assertEquals(customer3, customerService.getCustomerById(3));
		assertEquals(customer4, customerService.getCustomerById(4));
		
		Mockito.verify(customerRepository, Mockito.times(0)).findById(Mockito.anyLong());
		Mockito.verify(customerRepository, Mockito.times(1)).findAll();
		
		assertEquals(4, cache.findAll().size());
		assertTrue(cache.isEnabled());
		customerService.disable();
		assertEquals(0, cache.findAll().size());
		assertFalse(cache.isEnabled());
	}
    
    @Test
    public void testGetCustomerById()
    {
    	Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
    	assertEquals(customer1, customerService.getCustomerById(1));
    	
    	//Activate the cache
    	Mockito.when(customerRepository.findAll()).thenReturn(customers());
    	customerService.getCustomers();
		assertEquals(customer2, customerService.getCustomerById(2));
		assertEquals(customer3, customerService.getCustomerById(3));
		
		Mockito.verify(customerRepository, Mockito.times(1)).findById(Mockito.anyLong());
		Mockito.verify(customerRepository, Mockito.times(1)).findAll();
    }
	
	private List<Customer> customers()
	{
		List<Customer> result = new ArrayList<Customer>();
		result.add(customer1);
		result.add(customer2);
		result.add(customer3);
		return result;
	}
}
