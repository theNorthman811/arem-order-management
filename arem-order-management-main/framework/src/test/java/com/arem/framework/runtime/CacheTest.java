package com.arem.framework.runtime;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.arem.core.model.Customer;
import com.arem.core.model.Provider;
import com.arem.core.model.Seller;
import com.arem.core.model.User;
import com.arem.framework.Iterables;


public class CacheTest
{
	
	@Test
	public void TestCustomersCache() throws Exception
	{
		Customer c1 = new Customer(1, "Samir", "Khelfane");
		Customer c2 = new Customer(2, "Said", "Haouche");
		Customer c3 = new Customer(3, "Ali", "Khelfane", "email1");
		
		Customers cache = new Customers();
		testCache(cache, c1, c2, c3);
		
		
		List<Customer> all = new ArrayList<Customer>();
    	all.add(c1); all.add(c2); all.add(c3);
    	
    	testPutAll(cache, all);
    	testDelete(cache, all, new Customer(2));
    	testSave(cache, all);
    	
    	List<Customer> toDelete = new ArrayList<Customer>();
    	toDelete.add(new Customer(1));
    	toDelete.add(new Customer(3));
    	testDelete(cache, all, toDelete);
	}
	
	@Test
	public void TestProvidersCache() throws Exception
	{
		Provider c1 = new Provider(1, "Samir", "Khelfane");
		Provider c2 = new Provider(2, "Said", "Haouche");
		Provider c3 = new Provider(3, "Ali", "Khelfane", "email1");
		
		Providers cache = new Providers();
		testCache(cache, c1, c2, c3);
		
		List<Provider> all = new ArrayList<Provider>();
    	all.add(c1); all.add(c2); all.add(c3);
    	
    	testPutAll(cache, all);
    	testDelete(cache, all, new Provider(2));
    	testSave(cache, all);
    	
    	List<Provider> toDelete = new ArrayList<Provider>();
    	toDelete.add(new Provider(1));
    	toDelete.add(new Provider(3));
    	testDelete(cache, all, toDelete);
	}
	
	@Test
	public void TestSellersCache() throws Exception
	{
		Seller c1 = new Seller(1, "Samir", "Khelfane");
		Seller c2 = new Seller(2, "Said", "Haouche");
		Seller c3 = new Seller(3, "Ali", "Khelfane", "email1", "pass", "06521");
		
		Sellers cache = new Sellers();
		testCache(cache, c1, c2, c3);
		
		
		List<Seller> all = new ArrayList<Seller>();
    	all.add(c1); all.add(c2); all.add(c3);
    	
    	testPutAll(cache, all);
    	testDelete(cache, all, new Seller(2));
    	testSave(cache, all);
    	
    	List<Seller> toDelete = new ArrayList<Seller>();
    	toDelete.add(new Seller(1));
    	toDelete.add(new Seller(3));
    	testDelete(cache, all, toDelete);
	}
	
	private <T extends User> void testCache(Cache<T> cache, T object1, T object2, T object3) throws Exception
    {
    	assertFalse(cache.isEnabled());

    	cache.putAll(new ArrayList<T>());
    	assertTrue(cache.isEnabled());
    	
    	assertEquals(0,  cache.findAll().size());
    	
    	cache.save(object1);
    	cache.save(object2);
    	cache.save(object3);
    	
    	assertEquals(3,  cache.findAll().size());
    	assertEquals(1,  cache.findAll(x -> x.getFirstName().equals("Said")).size());
    	assertEquals(1,  cache.findAll(x -> x.getLastName().equals("Haouche")).size());
    	assertEquals(0,  cache.findAll(x -> x.getLastName().equals("Bellahcene")).size());
    	assertEquals(1,  cache.findAll(x -> x.getFirstName().equals("Ali") && x.getLastName().equals("Khelfane")).size());
    	assertEquals(2,  cache.findAll(x -> x.getLastName().equals("Khelfane")).size());
    	
    	assertEquals(1, cache.findById(1).getId());
    	assertEquals(2, cache.findById(2).getId());
    	assertEquals(3, cache.findById(3).getId());
    	assertNull(cache.findById(4));
    	
    	assertEquals(1, cache.findOne(x -> x.getLastName().equals("Khelfane")).getId());
    	assertEquals(2, cache.findOne(x -> x.getLastName().equals("Haouche")).getId());
    	assertEquals(3, cache.findOne(x -> x.getFirstName().equals("Ali")).getId());
    	assertNull(cache.findOne(x -> x.getLastName().equals("Bellahcene")));
    	
    	cache.save(object3);
    	assertEquals(3,  cache.findAll().size());
    	
    	assertEquals(2, cache.single(x -> x.getLastName().equals("Haouche")).getId());
    	    	
    	try 
    	{
			cache.single(x -> x.getLastName().equals("Bellahcene"));
			fail("Exception should be thrown");
		}
    	catch (Exception e)
    	{
			assertEquals("no element found", e.getMessage());
		}

    	try 
    	{
			cache.single(x -> x.getLastName().equals("Khelfane"));
			fail("Exception should be thrown");
		}
    	catch (Exception e)
    	{
			assertEquals("mors than one element found", e.getMessage());
		}
    	
    	cache.disable();
    	assertFalse(cache.isEnabled());
    	assertTrue(Iterables.isEmpty(cache.findAll()));
    }
	
	private <T extends User> void testPutAll(Cache<T> cache, List<T> all)
    {
		cache.putAll(all);
    	assertTrue(cache.isEnabled());
    	assertEquals(3, cache.findAll().size());
    	cache.disable();
    }
	
	private <T extends User> void testDelete(Cache<T> cache, List<T> all, T toDelete) throws Exception
    {
		cache.putAll(all);    	
    	cache.delete(toDelete);
    	assertEquals(2, cache.findAll().size());
    	assertNull(cache.findById(toDelete.getId()));
    	cache.disable();
    }
	
	private <T extends User> void testSave(Cache<T> cache, List<T> toSave) throws Exception
    {
		cache.putAll(new ArrayList<T>());
		cache.save(toSave);
		assertEquals(toSave.size(), cache.findAll().size());
		cache.disable();
    }
	
	private <T extends User> void testDelete(Cache<T> cache, List<T> all, List<T> toDelete) throws Exception
    {
		cache.putAll(all);
		cache.delete(toDelete);
		int remaining = all.size() - toDelete.size();
		assertEquals(remaining, cache.findAll().size());
		cache.disable();
    }
}

