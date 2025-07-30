package com.arem.api.providers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arem.core.model.Price;
import com.arem.core.model.Product;
import com.arem.core.model.Seller;
import com.arem.core.model.ValidationException;
import com.arem.dataservice.services.IPriceService;
import com.arem.dataservice.services.IProductService;
import com.arem.framework.differ.ProductDiffer;
import com.arem.productInput.contracts.PriceContract;
import com.arem.productInput.contracts.ProductContract;
import com.arem.productInput.roles.UserRole;
import com.arem.productInput.rules.ProductRule;

@Component
public class ProductProvider
{
	private static final Logger logger = LoggerFactory.getLogger(ProductProvider.class);

	@Autowired
	private IProductService productService;
	
	@Autowired
	private IPriceService priceService;
	
	@Autowired
	private ProductRule productRule;
	
	@Autowired
	private AuthProvider authProvider;
	
	
	public List<ProductContract> getProducts()
	{
		List<Product> all = productService.getProducts();
		List<ProductContract> result = new ArrayList<ProductContract>();
		for(Product product : all)
		{
			ProductContract contract = new ProductContract(product);
			result.add(contract);
		}
		return result;
	}
	
	public List<ProductContract> getPublicProducts()
	{
		List<Product> all = productService.getProducts();
		List<ProductContract> result = new ArrayList<ProductContract>();
		for(Product product : all)
		{
			// Pour les produits publics, on ne montre que les produits en stock
			if (product.getQuantity() > 0) {
				ProductContract contract = new ProductContract(product);
				result.add(contract);
			}
		}
		return result;
	}
	
	public ProductContract getProductById(long id)
	{
		Product product = productService.getProductById(id);
		if (product == null)
		{
			return null;
		}
		List<Price> prices = priceService.getPricesByProductId(product.getId());
		List<PriceContract> priceContracts = new ArrayList<PriceContract>();
		prices.forEach(p -> priceContracts.add(new PriceContract(p)));
		ProductContract contract = new ProductContract(product);
		contract.setPrices(priceContracts);
		return contract;
	}
	
	public ProductContract save(ProductContract contract) throws Exception
	{
		if (contract == null)
		{
			logger.error("Tentative de sauvegarde d'un produit null");
			throw new ValidationException("Le produit ne peut pas être null");
		}
		
		Object currentUser = authProvider.getCurrentUserOrCustomer();
		logger.debug("Tentative de sauvegarde d'un produit par l'utilisateur: {}", 
			currentUser instanceof Seller ? ((Seller) currentUser).getEmail() : "Customer");
		
		List<String> errors = new ArrayList<String>();
		
		// Seuls les Sellers peuvent créer/modifier des produits
		if (!(currentUser instanceof Seller) || !UserRole.CanUpdateProducts((Seller) currentUser))
		{
			logger.error("L'utilisateur n'a pas les droits pour créer/modifier des produits");
			throw new ValidationException("Vous n'avez pas les droits pour créer ou modifier des produits");
		}
		
		if (!productRule.validate(contract, errors))
		{
			logger.error("Erreurs de validation pour le produit: {}", errors);
			throw new ValidationException(errors);
		}
		
		Product product = contract.getModel();
		
		product.setModifDate(LocalDateTime.now());
		product.setModifSeller(currentUser instanceof Seller ? (Seller) currentUser : null);
		
		if (product.getId() == 0)
		{
			logger.debug("Création d'un nouveau produit: {}", product.getName());
			product.setCreationDate(LocalDateTime.now());
			product.setCreateSeller(currentUser instanceof Seller ? (Seller) currentUser : null);
			product.setVersion(1);
			
			if (!check(product, errors))
			{
				logger.error("Erreurs de validation business pour le produit: {}", errors);
				throw new ValidationException(errors);
			}

			try
			{
				Product savedProduct = productService.save(product);
				logger.info("Produit créé avec succès: {}", savedProduct.getId());
				return new ProductContract(savedProduct);
			}
			catch (Exception e)
			{
				logger.error("Erreur lors de la sauvegarde du produit", e);
				throw e;
			}
		}
		else
		{
			logger.debug("Mise à jour du produit: {}", product.getId());
			Product oldProduct = productService.getProductById(product.getId());
			if (oldProduct == null)
			{
				logger.error("Tentative de mise à jour d'un produit inexistant: {}", product.getId());
				throw new ValidationException("Le produit avec l'ID " + product.getId() + " n'existe pas");
			}
			
			product.setCreationDate(oldProduct.getCreationDate());
			product.setCreateSeller(oldProduct.getCreateSeller());
			int version = oldProduct.getVersion() + 1;
			product.setVersion(version);
			
			if (ProductDiffer.getDifferences(oldProduct, product).size() > 0)
			{
				if (!check(product, errors))
				{
					logger.error("Erreurs de validation business pour la mise à jour du produit: {}", errors);
					throw new ValidationException(errors);
				}

				try
				{
					Product savedProduct = productService.save(product);
					logger.info("Produit mis à jour avec succès: {}", savedProduct.getId());
					return new ProductContract(savedProduct);
				}
				catch (Exception e)
				{
					logger.error("Erreur lors de la mise à jour du produit", e);
					throw e;
				}
			}
			return contract;
		}
	}

	public ProductContract update(long id, ProductContract contract) throws Exception {
		if (contract == null) {
			throw new IllegalArgumentException("product could not be null");
		}
		
		Product existingProduct = productService.getProductById(id);
		if (existingProduct == null) {
			throw new IllegalArgumentException("Product not found with id: " + id);
		}
		
		contract.setId(id);
		return save(contract);
	}

	public void delete(long id) throws Exception
	{
		Object currentUser = authProvider.getCurrentUserOrCustomer();
		if (currentUser instanceof Seller && UserRole.CanUpdateProducts((Seller) currentUser))
		{
			Product product = productService.getProductById(id);
			if (product != null)
			{
				List<Price> prices = priceService.getPricesByProductId(product.getId());
				priceService.delete(prices);
				productService.delete(product);
			}
		}
	}
	
	private Boolean check(Product product, List<String> errors)
	{
		Product oldProduct = productService.getProductByReference(product.getReference());
		if (oldProduct != null && oldProduct.getId() != product.getId())
		{
			errors.add("Un autre produit avec la même référence existe déjà");
			return false;
		}
		
		oldProduct = productService.getProductByNameAndMarque(product.getName(), product.getMarque());
		if (oldProduct != null && oldProduct.getId() != product.getId())
		{
			errors.add("Un autre produit avec le même nom et la même marque existe déjà");
			return false;
		}
		
		return true;
	}
}
