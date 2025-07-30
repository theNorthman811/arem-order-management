package com.arem.productInput.rules;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arem.core.model.Measure;
import com.arem.core.model.Seller;
import com.arem.core.model.Side;
import com.arem.dataservice.services.ISellerService;
import com.arem.productInput.contracts.PriceContract;

@Component
public class PriceRule 
{
	
	@Autowired
	private ISellerService sellerService;
	
	public Boolean validate(PriceContract contract, List<String> errors)
	{
		if (contract == null)
		{
			throw new IllegalArgumentException("contract cannot be null");
		}
		
		if (contract.getSide() == Side.None)
		{
			errors.add("you must define the side of any price");
		}
		
		if (contract.getPrice() <= 0)
		{
			errors.add("defined amount is not valide");
		}
		
		if (contract.getMeasure() == Measure.None)
		{
			errors.add("you must define the unit measure of any price");
		}
		
		if (contract.getStartDate().isAfter(contract.getEndDate()))
		{
			errors.add("start date must be before end date");
		}
		
		if (contract.getEndDate().isBefore(LocalDateTime.now()))
		{
			errors.add("end date must be after or equal today");
		}
		
		if (contract.getModifSellerId() > 0)
		{
			Seller modifSeller = sellerService.getSellerById(contract.getModifSellerId());
			if (modifSeller == null)
			{
				errors.add("modif seller is invalid");
			}
			else
			{
				contract.getModel().setModifSeller(modifSeller);
			}
		}
		else
		{
			errors.add("modif seller is not filled");
		}
				
		return errors.isEmpty();
	}
	
	public Boolean validate(List<PriceContract> contracts, List<String> errors)
	{
		contracts.forEach(c -> validate(c, errors));
		if (errors.isEmpty())
		{
			int count = contracts.size();
			int j = 1;
			for(PriceContract contract1 : contracts)
			{
				for(int i = j; i < count; i++)
				{
					PriceContract contract2 = contracts.get(i);
					int otherPosition = i + 1;
					if (contract1.getId() == contract2.getId() && contract1.getId() > 0)
					{
						errors.add("Price position " + j + " and price position " + otherPosition + " have the same id");
					}
					else if (contract1.getSide() == contract2.getSide() && contract1.getMeasure() == contract2.getMeasure())
					{
						errors.add("Price position " + j + " and price position " + otherPosition + " have the same side and measure");
					}
				}
				j = j + 1;
			}
		}
		return errors.isEmpty();
	}
	
	
}
