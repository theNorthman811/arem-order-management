package com.arem.dataservice.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.arem.core.model.Seller;

@Service
public interface ISellerService
{

	public List<Seller> getSellers();
	
	public Seller getSellerById(long id);
	
	public Seller getSellerByUserName(String username);
	
	public Seller getSellerByPhoneNumber(String phoneNumber);
	
	public Seller getSellerByPickName(String pickName);
	
	public Seller getSellerByFirstNameAndLastNameAndPickName(String firstName, String lastName, String pickName);

	public Seller getSellerByEmail(String email);
	
	public long save(Seller seller) throws Exception;
	
	public void delete(Seller seller) throws Exception;
	
	public void disable();
	
}
