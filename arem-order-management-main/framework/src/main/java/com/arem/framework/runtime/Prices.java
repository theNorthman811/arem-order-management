package com.arem.framework.runtime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.arem.core.model.Price;
import com.arem.framework.Iterables;

@Component
public class Prices extends Cache<Price>
{
	
	public Prices()
	{
		super(true);
	}
	
	public List<Price> findByProductId(long productId)
	{
		if (cacheByGroup.containsKey(productId))
		{
			return cacheByGroup.get(productId);
		}
		return new ArrayList<Price>();
	}
	
	public Price getPriceById(long productId, long priceId)
	{
		List<Price> prices = findByProductId(productId);
		return Iterables.findOne(prices, price -> price.getId() == priceId);
	}
}
