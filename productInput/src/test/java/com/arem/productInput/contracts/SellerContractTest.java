package com.arem.productInput.contracts;

import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.Test;
import com.arem.core.model.Seller;

public class SellerContractTest extends UserContractTest<SellerContract>
{

	@Test
	public void testGettersAndSetters()
	{
		SellerContract contract = new SellerContract();
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
		Seller seller = new Seller(10);
		SellerContract contract = new SellerContract();
		seller.setFirstName("Ramdane");
		seller.setLastName("HAOUCHE");
		seller.setPickName("Adhegar");
		seller.setEmail("r.haouche@gmail.com");
		seller.setAddress("BERKOUKA, MAATKAS");
		seller.setPhoneNumber("0611099367");
		seller.setCreationDate(LocalDateTime.now());
		seller.setModifDate(LocalDateTime.now());
		seller.setPassword("pass1542");
		seller.setVersion(5);
		contract.setModel(seller);
		testSetModel(contract, seller);
	}
	
	@Test
	public void TestConstructor()
	{
		Seller seller = new Seller(10);
		seller.setFirstName("Ramdane");
		seller.setLastName("HAOUCHE");
		seller.setPickName("Adhegar");
		seller.setEmail("r.haouche@gmail.com");
		seller.setAddress("BERKOUKA, MAATKAS");
		seller.setPhoneNumber("0611099367");
		seller.setCreationDate(LocalDateTime.now());
		seller.setModifDate(LocalDateTime.now());
		seller.setPassword("pass1542");
		seller.setVersion(5);

		SellerContract contract = new SellerContract(seller);
		testSetModel(contract, seller);
	}
}
