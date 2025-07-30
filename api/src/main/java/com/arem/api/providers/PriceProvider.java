package com.arem.api.providers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.arem.core.model.Price;
import com.arem.core.model.Product;
import com.arem.core.model.Seller;
import com.arem.dataservice.services.IPriceService;
import com.arem.dataservice.services.IProductService;
import com.arem.framework.Iterables;
import com.arem.framework.differ.PriceDiffer;
import com.arem.productInput.contracts.PriceContract;
import com.arem.productInput.roles.UserRole;
import com.arem.productInput.rules.PriceRule;

@Component
public class PriceProvider
{

	@Autowired
	private IProductService productService;
	
	@Autowired
	private IPriceService priceService;
	
	@Autowired
	private PriceRule priceRule;
	
	
	@Autowired
	private AuthProvider authProvider;
	
	
	public void save(long productId, List<PriceContract> prices) throws Exception
	{
		Seller currentUser = authProvider.getCurrentUser();
		if (UserRole.CanUpdatePrices(currentUser))
		{
			List<String> errors = new ArrayList<String>();
			if (priceRule.validate(prices, errors))
			{
				Product product = productService.getProductById(productId);
				if (product == null) 
				{
					throw new IllegalArgumentException("could not find product with id : " + productId);
				}
				save(product, prices);
				// Clear cache after saving
				priceService.disable();
			}
			else
			{
				throw new IllegalArgumentException(String.join(";", errors));
			}
		}
		else
		{
			throw new IllegalArgumentException("You are not authorized to update prices");
		}
	}
	
	private void save(Product product, List<PriceContract> contracts) throws Exception
	{
		List<Price> oldPrices = priceService.getPricesByProductId(product.getId());
		List<Price> toDelete = new ArrayList<>();
		
		oldPrices.forEach(oldPrice ->
		{
			if (Iterables.findOne(contracts, p -> p.getId() == oldPrice.getId()) == null)
			{
				toDelete.add(oldPrice);
			}
		});
						
		List<Price> toSave = new ArrayList<Price>();
		for(PriceContract priceContract : contracts)
		{
			Price price = priceContract.getModel();
			price.setModifDate(LocalDateTime.now());
			price.setProductId(product.getId());
			if (price.getId() == 0)
			{
				price.setCreationDate(LocalDateTime.now());
				price.setCreateSeller(price.getModifSeller());
				price.setVersion(1);
				toSave.add(price);
			}
			else
			{
				Price old = priceService.getPriceById(price.getId(), product.getId());
				if (old == null)
				{
					throw new IllegalArgumentException("the price id specified is not valid : " + price.getId());
				}
				price.setCreationDate(old.getCreationDate());
				price.setCreateSeller(old.getCreateSeller());
				int version = old.getVersion() + 1;
				price.setVersion(version);
				if (PriceDiffer.getDifferences(old, price).size() > 0)
				{
					toSave.add(price);
				}
			}
			
		}
		if (!toDelete.isEmpty())
		{
			priceService.delete(toDelete);
		}
		if (!toSave.isEmpty())
		{
			priceService.save(toSave);
		}
		// Clear cache after all operations
		priceService.disable();
	}
	
	public List<PriceContract> getPrices() {
		List<Price> all = priceService.getPrices();
		List<PriceContract> result = new ArrayList<>();
		for(Price price : all) {
			result.add(new PriceContract(price));
		}
		return result;
	}
	
	public PriceContract getPriceById(long id) {
		Price price = priceService.getPriceById(id);
		if (price == null) {
			return null;
		}
		return new PriceContract(price);
	}
	
	public PriceContract save(PriceContract contract) throws Exception {
		if (contract == null) {
			throw new IllegalArgumentException("price could not be null");
		}
		
		Seller currentUser = authProvider.getCurrentUser();
		List<String> errors = new ArrayList<>();
		
		if (UserRole.CanUpdateProducts(currentUser)) {
			if (priceRule.validate(contract, errors)) {
				Price price = contract.getModel();
				
				price.setModifDate(LocalDateTime.now());
				price.setModifSeller(currentUser);
				
				if (price.getId() == 0) {
					price.setCreationDate(LocalDateTime.now());
					price.setCreateSeller(currentUser);
					price.setVersion(1);
					if (check(price, errors)) {
						long id = priceService.save(price);
						price.setId(id);
						// Clear cache after saving
						priceService.disable();
						return new PriceContract(price);
					} else {
						throw new IllegalArgumentException(String.join(";", errors));
					}
				} else {
					Price oldPrice = priceService.getPriceById(price.getId());
					if (oldPrice == null) {
						throw new IllegalArgumentException("the price id specified is not valid : " + price.getId());
					}
					price.setCreationDate(oldPrice.getCreationDate());
					price.setCreateSeller(oldPrice.getCreateSeller());
					int version = oldPrice.getVersion() + 1;
					price.setVersion(version);
					
					if (check(price, errors)) {
						long id = priceService.save(price);
						price.setId(id);
						// Clear cache after saving
						priceService.disable();
						return new PriceContract(price);
					} else {
						throw new IllegalArgumentException(String.join(";", errors));
					}
				}
			} else {
				throw new IllegalArgumentException(String.join(";", errors));
			}
		} else {
			throw new IllegalArgumentException("You are not authorized to save or update prices");
		}
	}
	
	public PriceContract update(long id, PriceContract contract) throws Exception {
		if (contract == null) {
			throw new IllegalArgumentException("price could not be null");
		}
		
		Price existingPrice = priceService.getPriceById(id);
		if (existingPrice == null) {
			throw new IllegalArgumentException("Price not found with id: " + id);
		}
		
		contract.setId(id);
		PriceContract result = save(contract);
		// Clear cache after updating
		priceService.disable();
		return result;
	}
	
	public void delete(long id) throws Exception {
		Seller currentUser = authProvider.getCurrentUser();
		if (UserRole.CanUpdatePrices(currentUser)) {
			Price price = priceService.getPriceById(id);
			if (price != null) {
				priceService.delete(price);
				// Clear cache after deleting
				priceService.disable();
			}
		} else {
			throw new IllegalArgumentException("You are not authorized to delete prices");
		}
	}
	
	private Boolean check(Price price, List<String> errors) {
		if (price.getProduct() == null) {
			errors.add("Product is required");
			return false;
		}
		
		Product product = productService.getProductById(price.getProduct().getId());
		if (product == null) {
			errors.add("Product not found");
			return false;
		}
		
		return true;
	}
}
