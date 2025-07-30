package com.arem.framework.differ;

import java.util.ArrayList;
import java.util.List;

import com.arem.core.model.Price;

public class PriceDiffer
{
	
	public static List<Difference> getDifferences(Price _old, Price _new)
	{
		List<Difference> result = new ArrayList<Difference>();
		
		if (_old == null || _new == null)
		{
			return result;
		}
		
		if (!_new.getEndDate().equals(_old.getEndDate()))
		{
			result.add(new Difference("EndDate", _old.getEndDate().toString(), _new.getEndDate().toString()));
		}
		
		if (!_new.getMeasure().equals(_old.getMeasure()))
		{
			result.add(new Difference("Measure", _old.getMeasure().toString(), _new.getMeasure().toString()));
		}
		
		if (_new.getPrice() != _old.getPrice())
		{
			result.add(new Difference("Price", ""+_old.getPrice(), ""+_new.getPrice()));
		}
		
		if (!_new.getSide().equals(_old.getSide()))
		{
			result.add(new Difference("Side", _old.getSide().toString(), _new.getSide().toString()));
		}
		
		if (!_new.getStartDate().equals(_old.getStartDate()))
		{
			result.add(new Difference("StartDate", _old.getStartDate().toString(), _new.getStartDate().toString()));
		}
		
		return result;
	}

}
