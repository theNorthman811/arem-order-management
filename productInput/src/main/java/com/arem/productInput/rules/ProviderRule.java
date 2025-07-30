package com.arem.productInput.rules;

import java.util.List;

import org.springframework.stereotype.Component;
import com.arem.productInput.contracts.ProviderContract;


@Component
public class ProviderRule extends UserRule<ProviderContract>
{
	
	public Boolean validate(ProviderContract contract, List<String> errors)
	{
		return super.validate(contract, errors);
	}
	
}
