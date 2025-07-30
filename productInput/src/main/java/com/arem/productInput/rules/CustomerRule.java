package com.arem.productInput.rules;

import java.util.List;

import org.springframework.stereotype.Component;
import com.arem.productInput.contracts.CustomerContract;


@Component
public class CustomerRule extends UserRule<CustomerContract>
{
	
	public Boolean validate(CustomerContract contract, List<String> errors)
	{
		return super.validate(contract, errors);
	}	
}
