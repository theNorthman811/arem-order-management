package com.arem.productInput.rules;

import java.util.List;
import org.springframework.stereotype.Component;

import com.arem.core.model.Measure;
import com.arem.framework.StringHelper;
import com.arem.productInput.contracts.ProductContract;


@Component
public class ProductRule
{
			
	private final int MINIMUM_NAME_LENGTH = 4;
	
	private final int MINIMUM_REFERENCE_LENGTH = 4;
	
	
	public Boolean validate(ProductContract contract, List<String> errors)
	{
		
		if (contract == null)
		{
			throw new IllegalArgumentException("contract cannot be null");
		}
		
		if (StringHelper.isNullOrEmpty(contract.getName()))
		{
			errors.add("Product name could not be null");
		}
		
		else if (contract.getName().length() < MINIMUM_NAME_LENGTH)
		{
			errors.add("given product name is not valid");
		}
		
		if (StringHelper.isNullOrEmpty(contract.getReference()))
		{
			errors.add("Product reference could not be null");
		}
		
		else if (contract.getReference().length() < MINIMUM_REFERENCE_LENGTH)
		{
			errors.add("given product reference is not valid");
		}
		
		if (contract.getQuantity() <= 0)
		{
			errors.add("product quantity could not be less than 0");
		}
		
		if (contract.getMeasure() == Measure.None)
		{
			errors.add("You must define product Measure");
		}
		
		return errors.isEmpty();	
	}
}
