package com.arem.dataservice.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.arem.core.model.Price;

@Service
public interface IPriceService {

	public List<Price> getPrices();
	
	public Price getPriceById(long id);
	
	public Price getPriceById(long id, long productId);
	
	public List<Price> getPricesByProductId(long productId);
	
	public long save(Price price) throws Exception;
	
	public void save(List<Price> prices) throws Exception;
	
	public void delete(Price price) throws Exception;
	
	public void delete(List<Price> prices) throws Exception;
	
	public void disable();
	
	public double getCurrentSellingPrice(long productId);
	
}
