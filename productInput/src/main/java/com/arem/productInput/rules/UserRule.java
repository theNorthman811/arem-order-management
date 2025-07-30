package com.arem.productInput.rules;

import java.util.List;
import org.springframework.stereotype.Service;
import com.arem.framework.Utilities;
import com.arem.productInput.contracts.UserContract;

@Service
@SuppressWarnings("rawtypes")
public abstract class UserRule<T extends UserContract>
{
	private static final String NAME_PATTERN = "[a-zA-Z]+[ ]?[a-zA-Z]+";
	private static final String ADDRESS_PATTERN = "([0-9]{0,3}[ ])?([a-zA-Z]+[ ]?[a-zA-Z]+)+([ ][0-9]{5}[ ][a-zA-Z]+)?([ ][a-zA-Z]+)?";
	private static final String PHONE_NUMBER_PATTERN = "(0|\\+213|00213|0033|\\+33)[1-9]([0-9]{2}){4}";

	public Boolean validate(T contract, List<String> errors)
	{
		if (contract == null)
		{
			throw new IllegalArgumentException("contract cannot be null");
		}
		
		if (!Utilities.match(contract.getFirstName(), NAME_PATTERN, 3, 15))
		{
			errors.add("given first name is invalid");
		}
		
		if (!Utilities.match(contract.getLastName(), NAME_PATTERN, 3, 15))
		{
			errors.add("given last name is invalid");
		}
		
		if (!Utilities.match(contract.getAddress(), ADDRESS_PATTERN, 4, 80))
		{
			errors.add("given address is invalid");
		}
		
		if (!Utilities.match(contract.getPhoneNumber(), PHONE_NUMBER_PATTERN))
		{
			errors.add("given phone number is invalid");
		}
		
		return errors.isEmpty();
	}
}
