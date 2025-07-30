package com.arem.productInput.contracts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import org.junit.Test;
import com.arem.core.model.Provider;
import com.arem.core.model.Seller;

public class ProviderContractTest extends UserContractTest<ProviderContract>
{

	@Test
	public void testGettersAndSetters()
	{
		ProviderContract contract = new ProviderContract();
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
		Provider provider = new Provider(10);
		ProviderContract contract = new ProviderContract();
		provider.setFirstName("Ramdane");
		provider.setLastName("HAOUCHE");
		provider.setPickName("Adhegar");
		provider.setAddress("BERKOUKA, MAATKAS");
		provider.setPhoneNumber("0611099367");
		provider.setCreationDate(LocalDateTime.now());
		provider.setModifDate(LocalDateTime.now());
		provider.setModifSeller(new Seller(2));
		provider.setCreateSeller(new Seller(3));
		provider.setVersion(5);
		contract.setModel(provider);
		testSetModel(contract, provider);
		assertEquals(2, contract.getModifSellerId());
		assertEquals(3, contract.getCreateSellerId());
	}
	
	@Test
	public void TestConstructor()
	{
		Provider provider = new Provider(10);
		provider.setFirstName("Ramdane");
		provider.setLastName("HAOUCHE");
		provider.setPickName("Adhegar");
		provider.setAddress("BERKOUKA, MAATKAS");
		provider.setPhoneNumber("0611099367");
		provider.setCreationDate(LocalDateTime.now());
		provider.setModifDate(LocalDateTime.now());
		provider.setModifSeller(new Seller(2));
		provider.setCreateSeller(new Seller(3));
		provider.setVersion(5);

		ProviderContract contract = new ProviderContract(provider);
		testSetModel(contract, provider);
		assertEquals(2, contract.getModifSellerId());
		assertEquals(3, contract.getCreateSellerId());
	}
}
