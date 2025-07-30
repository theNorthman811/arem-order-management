package com.arem.dataservice.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.arem.core.model.Product;

@Service
public interface IProductService 
{
    
	public List<Product> getProducts();
	
	public Product getProductById(long id);
	
	public Product getProductByNameAndMarque(String name, String marque);
	
	public Product getProductByReference(String reference);
	
	public Product save(Product product) throws Exception;
	
	public void delete(Product product) throws Exception;
	
	public void delete(List<Product> products) throws Exception;
	
	public void disable();
	
}
