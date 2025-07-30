package com.arem.dataservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.arem.core.model.Product;
import com.arem.dataservice.repositories.IProductRepository;
import com.arem.framework.StringHelper;
import com.arem.framework.runtime.Products;

@Component
public class ProductService implements IProductService
{

	@Autowired
	private IProductRepository productRepository;
	
	@Autowired
	private Products cache;
	
	private final Object sync = new Object();

	private void enableCache()
	{
		if (!cache.isEnabled())
		{
			synchronized(sync)
			{
				if (!cache.isEnabled())
				{
					Iterable<Product> all = productRepository.findAll();
					cache.putAll(all);
				}
			}
		}
	}
	
	@Override
	public List<Product> getProducts()
	{
		enableCache();
		return cache.findAll();
	}

	@Override
	public Product getProductById(long id)
	{
		enableCache();
		return cache.findById(id);
	}
	
	
	@Override
	public Product getProductByNameAndMarque(String name, String marque)
	{
		enableCache();
		if (StringHelper.isNullOrEmpty(name))
		{
			return null;
		}
		if (StringHelper.isNullOrEmpty(marque))
		{
			return cache.findOne(p -> p.getName().trim().toUpperCase().equals(name.trim().toUpperCase()));
		}
		else
		{
			return cache.findOne(p -> StringHelper.isNotNullOrEmpty(p.getMarque()) && p.getName().trim().toUpperCase().equals(name.trim().toUpperCase()) &&
					p.getMarque().trim().toUpperCase().equals(marque.trim().toUpperCase()));
		}
	}
		
	@Override
	public Product getProductByReference(String reference)
	{
		enableCache();
		if (StringHelper.isNullOrEmpty(reference))
		{
			return null;
		}
		return cache.findOne(p -> p.getReference().trim().toUpperCase().equals(reference.trim().toUpperCase()));
	}

	@Override
	public Product save(Product product) throws Exception
	{
		Product result = productRepository.save(product);
		if (cache.isEnabled())
		{
			try
			{
				cache.save(result);
			}
			catch(Exception ex)
			{
				disable();
			}
		}
		return result;
	}
	
	@Override
	public void delete(Product product) throws Exception
	{
		productRepository.delete(product);
		if (cache.isEnabled())
		{
			try
			{
				cache.delete(product);
			}
			catch(Exception ex)
			{
				disable();
			}
		}
	}
	
	@Override
	public void delete(List<Product> products) throws Exception
	{
		productRepository.deleteAll(products);
		if (cache.isEnabled())
		{
			try
			{
				cache.delete(products);
			}
			catch(Exception ex)
			{
				disable();
			}
		}
	}

	@Override
	public void disable() 
	{
		cache.disable();
	}

}
