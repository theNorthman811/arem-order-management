package com.arem.productInput.contracts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import com.arem.core.model.Measure;
import com.arem.core.model.Side;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductContractTest
{
	
	@Test
	public void TestGettersAndSetters() throws JsonProcessingException
	{
		ProductContract product = new ProductContract();
		
		product.setComment("Test product comment");
		product.setDescription("Test product description");
		product.setReference("159856233");
		product.setName("Javel 100ML");
		product.getModel().setCreationDate(LocalDateTime.now());
		product.getModel().setModifDate(LocalDateTime.now());
		
		PriceContract price = new PriceContract();
		price.setEndDate(LocalDateTime.now());
		price.setStartDate(LocalDateTime.now());
		price.setPrice(100);
		price.setSide(Side.Sell);
		price.setMeasure(Measure.Unit);
		
		List<PriceContract> prices = new ArrayList<>();
		prices.add(price);
		
		product.setPrices(prices);
		
		ObjectMapper mapper = new ObjectMapper();
		String c = mapper.writeValueAsString(product);
		System.out.println(c);
	}

}
