package com.arem.framework.differ;

import java.util.ArrayList;
import java.util.List;
import com.arem.core.model.Product;

public class ProductDiffer 
{
	
	public static List<Difference> getDifferences(Product _old, Product _new)
	{
		List<Difference> result = new ArrayList<Difference>();
		
		if (_old == null || _new == null)
		{
			return result;
		}
		
		String oldComment = _old.getComment();
		String newComment = _new.getComment();
		if ((oldComment == null && newComment != null) || 
		    (oldComment != null && !oldComment.equals(newComment)))
		{
			result.add(new Difference("Comment", oldComment, newComment));
		}
		
		if (!_new.getDescription().equals(_old.getDescription()))
		{
			result.add(new Difference("Description", _old.getDescription(), _new.getDescription()));
		}
		
		if (!_new.getName().equals(_old.getName()))
		{
			result.add(new Difference("Name", _old.getName(), _new.getName()));
		}
		
		if (!_new.getReference().equals(_old.getReference()))
		{
			result.add(new Difference("Reference", _old.getReference(), _new.getReference()));
		}
		
		if (!_new.getMarque().equals(_old.getMarque()))
		{
			result.add(new Difference("Marque", _old.getMarque(), _new.getMarque()));
		}
		
		if (_new.getQuantity() != _old.getQuantity())
		{
			result.add(new Difference("Quantity", Double.toString(_old.getQuantity()), Double.toString(_new.getQuantity())));
		}
		
		if (!_new.getMeasure().equals(_old.getMeasure()))
		{
			result.add(new Difference("Measure", _old.getMeasure().toString(), _new.getMeasure().toString()));
		}
		
		return result;
	}

}
