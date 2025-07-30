package com.arem.dataservice.services;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.arem.core.model.Price;
import com.arem.core.model.Side;
import com.arem.dataservice.repositories.IPriceRepository;
import com.arem.framework.runtime.Prices;

@Service
public class PriceService implements IPriceService
{
	private static final Logger logger = LoggerFactory.getLogger(PriceService.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private IPriceRepository priceRepository;
	
	@Autowired
	private Prices cache;
	
	private final Object sync = new Object();

	private void enableCache()
	{
		if (!cache.isEnabled())
		{
			synchronized(sync)
			{
				if (!cache.isEnabled())
				{
					logger.info("Initializing price cache...");
					try {
						Iterable<Price> all = priceRepository.findAll();
						cache.putAll(all);
						logger.info("Price cache initialized successfully");
					} catch (Exception e) {
						logger.error("Error initializing price cache: {}", e.getMessage());
						throw new RuntimeException("Failed to initialize price cache", e);
					}
				}
			}
		}
	}
	
	@Override
	public List<Price> getPrices()
	{
		enableCache();
		return cache.findAll();
	}

	@Override
	public Price getPriceById(long id)
	{
		enableCache();
		return cache.findById(id);
	}

	@Override
	public Price getPriceById(long id, long productId)
	{
		enableCache();
		return cache.getPriceById(productId, id);
	}

	@Override
	public long save(Price price) throws Exception
	{
		if (price == null)
		{
			throw new Exception("Le prix ne peut pas être null");
		}
		
		synchronized(sync) {
			Price savedPrice = priceRepository.save(price);
			if (savedPrice != null)
			{
				// Désactiver le cache avant de sauvegarder pour forcer un rechargement
				disable();
				cache.save(savedPrice);
				return savedPrice.getId();
			}
			return 0;
		}
	}

	@Override
	public void delete(Price price) throws Exception
	{
		if (price == null)
		{
			throw new Exception("Le prix ne peut pas être null");
		}
		
		synchronized(sync) {
			priceRepository.delete(price);
			disable();
			cache.delete(price);
		}
	}
	
	@Override
	public void disable()
	{
		logger.info("Disabling price cache");
		synchronized(sync) {
			cache.disable();
		}
	}
	
	// Additional utility methods not in the interface
	@Override
	public List<Price> getPricesByProductId(long productId)
	{
		enableCache();
		return cache.findByProductId(productId);
	}

	@Override
	public void save(List<Price> prices) throws Exception
	{
		if (prices == null || prices.isEmpty())
		{
			throw new Exception("La liste de prix ne peut pas être null ou vide");
		}
		
		synchronized(sync) {
			Iterable<Price> savedPrices = priceRepository.saveAll(prices);
			// Désactiver le cache avant de sauvegarder pour forcer un rechargement
			disable();
			cache.save(savedPrices);
		}
	}

	@Override
	public void delete(List<Price> prices) throws Exception
	{
		if (prices == null || prices.isEmpty())
		{
			throw new Exception("La liste de prix ne peut pas être null ou vide");
		}
		
		synchronized(sync) {
			priceRepository.deleteAll(prices);
			disable();
			cache.delete(prices);
		}
	}

	@Override
	public double getCurrentSellingPrice(long productId) {
		synchronized(sync) {
			// Force un rechargement du cache
			disable();
			enableCache();
			
			LocalDateTime now = LocalDateTime.now();
			logger.info("Searching price for product {} at date {}", productId, now.format(formatter));
			
			// Log database state
			logger.info("Checking database state...");
			List<Price> dbPrices = priceRepository.findByProductId(productId);
			logger.info("Found {} prices in database for product {}", dbPrices.size(), productId);
			for (Price p : dbPrices) {
				logger.info("DB Price - ID: {}, Side: {}, StartDate: {}, EndDate: {}, Amount: {}", 
					p.getId(), p.getSide(), p.getStartDate(), p.getEndDate(), p.getPrice());
			}
			
			// Log cache state
			logger.info("Checking cache state...");
			List<Price> prices = cache.findByProductId(productId);
			logger.info("Found {} prices in cache for product {}", prices.size(), productId);
			
			if (prices.isEmpty()) {
				logger.error("No prices found in cache for product {}", productId);
				throw new RuntimeException("No prices found for product " + productId);
			}
			
			logger.info("Validating prices from cache...");
			for (Price p : prices) {
				logger.info("Cache Price - ID: {}, Side: {}, StartDate: {}, EndDate: {}, Amount: {}", 
					p.getId(), p.getSide(), p.getStartDate(), p.getEndDate(), p.getPrice());
					
				boolean isSellSide = Side.Sell.equals(p.getSide());
				boolean isValidStartDate = !p.getStartDate().isAfter(now);
				boolean isValidEndDate = !p.getEndDate().isBefore(now);
				
				logger.info("Price {} validation - IsSellSide: {}, ValidStartDate: {}, ValidEndDate: {}", 
					p.getId(), isSellSide, isValidStartDate, isValidEndDate);
			}
			
			List<Price> validPrices = prices.stream()
				.filter(p -> {
					boolean isSellSide = Side.Sell.equals(p.getSide());
					boolean isValidStartDate = !p.getStartDate().isAfter(now);
					boolean isValidEndDate = !p.getEndDate().isBefore(now);
					return isSellSide && isValidStartDate && isValidEndDate;
				})
				.collect(Collectors.toList());
				
			logger.info("Found {} valid prices after filtering", validPrices.size());
			
			if (validPrices.isEmpty()) {
				logger.error("No valid selling price found for product {}", productId);
				throw new RuntimeException("No valid selling price found for product " + productId + 
					". Make sure there is a price with side 'Sell' and valid dates.");
			}
			
			double price = validPrices.get(0).getPrice();
			logger.info("Selected price {} for product {}", price, productId);
			return price;
		}
	}
}
