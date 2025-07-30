package com.arem.api.providers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.arem.core.model.Seller;
import com.arem.dataservice.services.ISellerService;
import com.arem.productInput.contracts.SellerContract;
import com.arem.productInput.roles.UserRole;
import com.arem.productInput.rules.SellerRule;

@Component
public class SellerProvider
{
	
	@Autowired
	private ISellerService sellerService;
	
	@Autowired
	private SellerRule sellerRule;
	
	@Autowired
	private AuthProvider authProvider;
	
	
	public List<SellerContract> getSellers()
	{
		List<Seller> all = sellerService.getSellers();
		List<SellerContract> result = new ArrayList<>();
		for(Seller seller : all)
		{
			result.add(new SellerContract(seller));
		}
		return result;
	}
	
	public SellerContract getSellerById(long id)
	{
		Seller seller = sellerService.getSellerById(id);
		if (seller == null)
		{
			return null;
		}
		return new SellerContract(seller);
	}
	
	
	public SellerContract save(SellerContract contract) throws Exception
	{
		if (contract == null)
		{
			throw new IllegalArgumentException("seller could not be null");
		}
		
		Seller currentUser = authProvider.getCurrentUser();
		List<String> errors = new ArrayList<>();
		
		if (UserRole.CanUpdateSellers(currentUser))
		{
			if (sellerRule.validate(contract, errors))
			{
				Seller seller = contract.getModel();
				
				seller.setModifDate(LocalDateTime.now());
				seller.setModifSeller(currentUser);
				
				if (seller.getId() == 0)
				{
					seller.setCreationDate(LocalDateTime.now());
					seller.setCreateSeller(currentUser);
					seller.setVersion(1);
					if (check(seller, errors))
					{
						long id = sellerService.save(seller);
						seller.setId(id);
						return new SellerContract(seller);
					}
					else
					{
						throw new IllegalArgumentException(String.join(";", errors));
					}
				}
				else
				{
					Seller oldSeller = sellerService.getSellerById(seller.getId());
					if (oldSeller == null)
					{
						throw new IllegalArgumentException("the seller id specified is not valid : " + seller.getId());
					}
					seller.setCreationDate(oldSeller.getCreationDate());
					seller.setCreateSeller(oldSeller.getCreateSeller());
					int version = oldSeller.getVersion() + 1;
					seller.setVersion(version);
					
					if (check(seller, errors))
					{
						long id = sellerService.save(seller);
						seller.setId(id);
						return new SellerContract(seller);
					}
					else
					{
						throw new IllegalArgumentException(String.join(";", errors));
					}
				}
			}
			else
			{
				throw new IllegalArgumentException(String.join(";", errors));
			}
		}
		else
		{
			throw new IllegalArgumentException("You are not authorized to save or update sellers");
		}
	}
	
	public SellerContract update(long id, SellerContract contract) throws Exception
	{
		if (contract == null)
		{
			throw new IllegalArgumentException("seller could not be null");
		}
		
		Seller existingSeller = sellerService.getSellerById(id);
		if (existingSeller == null)
		{
			throw new IllegalArgumentException("Seller not found with id: " + id);
		}
		
		contract.setId(id);
		return save(contract);
	}
	
	public void delete(long id) throws Exception
	{
		Seller currentUser = authProvider.getCurrentUser();
		if (UserRole.CanUpdateSellers(currentUser))
		{
			Seller seller = sellerService.getSellerById(id);
			if (seller != null)
			{
				sellerService.delete(seller);
			}
			else
			{
				throw new IllegalArgumentException("Seller not found with id: " + id);
			}
		}
		else
		{
			throw new IllegalArgumentException("You are not authorized to delete sellers");
		}
	}
	
	private Boolean check(Seller seller, List<String> errors)
	{
		Seller oldSeller = sellerService.getSellerByPhoneNumber(seller.getPhoneNumber());
		if (oldSeller != null && oldSeller.getId() != seller.getId())
		{
			errors.add("Another seller with same phone number already exists");
		}
		
		oldSeller = sellerService.getSellerByEmail(seller.getEmail());
		if (oldSeller != null && oldSeller.getId() != seller.getId())
		{
			errors.add("Another seller with same email already exists");
		}
		
		return errors.isEmpty();
	}
	
}
