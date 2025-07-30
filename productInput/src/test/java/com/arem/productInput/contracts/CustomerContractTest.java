package com.arem.productInput.contracts;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

import com.arem.core.model.Customer;
import com.arem.core.model.Seller;

public class CustomerContractTest extends UserContractTest<CustomerContract>
{
	
	@Test
	public void testGettersAndSetters()
	{
		CustomerContract contract = new CustomerContract();
		assertNotNull(contract.getModel());
		testSetFirstName(contract, "Samir");
		testSetLastName(contract, "Khelfane");
		testSetPickName(contract, "adhegar");
		testSetAddress(contract, "BERKOUKA, MAATKAS");
		testSetPhoneNumber(contract, "002135524858545");
	}
	
	@Test
	public void TestSetModel()
	{
		Customer customer = new Customer(10);
		CustomerContract contract = new CustomerContract();
		customer.setFirstName("Ramdane");
		customer.setLastName("HAOUCHE");
		customer.setPickName("Adhegar");
		customer.setAddress("BERKOUKA, MAATKAS");
		customer.setPhoneNumber("0611099367");
		customer.setCreationDate(LocalDateTime.now());
		customer.setModifDate(LocalDateTime.now());
		customer.setModifSeller(new Seller(2));
		customer.setCreateSeller(new Seller(3));
		customer.setVersion(5);
		contract.setModel(customer);
		testSetModel(contract, customer);
		assertEquals(2, contract.getModifSellerId());
		assertEquals(3, contract.getCreateSellerId());
	}
	
	@Test
	public void TestConstructor()
	{
		Customer customer = new Customer(10);
		customer.setFirstName("Ramdane");
		customer.setLastName("HAOUCHE");
		customer.setPickName("Adhegar");
		customer.setAddress("BERKOUKA, MAATKAS");
		customer.setPhoneNumber("0611099367");
		customer.setCreationDate(LocalDateTime.now());
		customer.setModifDate(LocalDateTime.now());
		customer.setModifSeller(new Seller(2));
		customer.setCreateSeller(new Seller(3));
		customer.setVersion(5);

		CustomerContract contract = new CustomerContract(customer);
		testSetModel(contract, customer);
		assertEquals(2, contract.getModifSellerId());
		assertEquals(3, contract.getCreateSellerId());
	}
}
