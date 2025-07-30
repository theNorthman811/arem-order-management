package com.arem.productInput.rules;

import java.util.List;

import org.springframework.stereotype.Service;
import com.arem.framework.StringHelper;
import com.arem.framework.Utilities;
import com.arem.productInput.contracts.SellerContract;

@Service
public class SellerRule extends UserRule<SellerContract>
{
	
	private static final String EMAIL_PATTERN = "([\\w]+[-\\.]?[\\w]+)+@(hotmail|gmail|outlook|yahoo)\\.(com|fr)";

	public Boolean validate(SellerContract contract, List<String> errors)
	{		
		super.validate(contract, errors);		
		
		if (!Utilities.match(contract.getEmail(), EMAIL_PATTERN, 10, 40))
		{
			errors.add("given address mail is invalid");
		}
		
		if (contract.getId() != 0)
		{
			return errors.isEmpty();
		}
		
		if (StringHelper.isNullOrEmpty(contract.getModel().getPassword()))
		{
			errors.add("password could not be empty");
		}
		
		else if (contract.getModel().getPassword().length() < 8)
		{
			errors.add("given password is not valid");
		}
				
		return errors.isEmpty();
		
	}
}
