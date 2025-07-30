package com.arem.productInput.contracts;

import static org.junit.Assert.*;

import com.arem.core.model.User;

@SuppressWarnings("rawtypes")
public abstract class UserContractTest<T extends UserContract>
{
	
	
	protected void testSetFirstName(T contract, String firstName)
	{
		contract.setFirstName(firstName);
		assertEquals(firstName, contract.getFirstName());
		assertEquals(firstName, contract.getModel().getFirstName());
	}
	
	protected void testSetLastName(T contract, String lastName)
	{
		contract.setLastName(lastName);
		assertEquals(lastName, contract.getLastName());
		assertEquals(lastName, contract.getModel().getLastName());
	}
	
	protected void testSetPickName(T contract, String pickName)
	{
		contract.setPickName(pickName);
		assertEquals(pickName, contract.getPickName());
		assertEquals(pickName, contract.getModel().getPickName());
	}
	
	protected void testSetAddress(T contract, String address)
	{
		contract.setAddress(address);
		assertEquals(address, contract.getAddress());
		assertEquals(address, contract.getModel().getAddress());
	}
	
	
	protected void testSetPhoneNumber(T contract, String phone)
	{
		contract.setPhoneNumber(phone);
		assertEquals(phone, contract.getPhoneNumber());
		assertEquals(phone, contract.getModel().getPhoneNumber());
	}
	
	protected void testSetModel(T contract, User user)
	{
		assertEquals(user.getId(), contract.getId());
		assertEquals(user.getFirstName(), contract.getFirstName());
		assertEquals(user.getLastName(), contract.getLastName());
		assertEquals(user.getPhoneNumber(), contract.getPhoneNumber());
		assertEquals(user.getModifDate(), contract.getModifDate());
		assertEquals(user.getCreationDate(), contract.getCreationDate());
		assertEquals(user.getPickName(), contract.getPickName());
		assertEquals(user.getVersion(), contract.getVersion());
		assertEquals(user.getAddress(), contract.getAddress());
	}
}





