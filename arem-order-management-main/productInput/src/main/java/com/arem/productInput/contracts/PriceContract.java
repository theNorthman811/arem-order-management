package com.arem.productInput.contracts;

import java.time.LocalDateTime;

import com.arem.core.model.Measure;
import com.arem.core.model.Price;
import com.arem.core.model.Seller;
import com.arem.core.model.Side;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.arem.core.model.Product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public class PriceContract
{
	
	private Price model;
	
	public PriceContract()
	{
		model = new Price();
	}
	
	public PriceContract(Price model)
	{
		this.model = model;
	}
	
	public long getId()
	{
		return model.getId();
	}
	
	public void setId(long id)
	{
		model.setId(id);
	}
	
	public double getPrice()
	{
		return model.getPrice();
	}
	
	public void setPrice(double price)
	{
		model.setPrice(price);
	}
	
	public Measure getMeasure()
	{
		return model.getMeasure();
	}
	
	public void setMeasure(Measure measure)
	{
		model.setMeasure(measure);
	}
	
	public Side getSide()
	{
		return model.getSide();
	}
	
	public void setSide(Side side)
	{
		model.setSide(side);
	}
	
	@NotNull(message = "Le produit est obligatoire")
	public Product getProduct()
	{
		return model.getProduct();
	}
	
	public void setProduct(Product product)
	{
		model.setProduct(product);
	}
	
	@NotNull(message = "Le prix est obligatoire")
	@Min(value = 0, message = "Le prix ne peut pas être négatif")
	public Double getAmount()
	{
		return model.getAmount();
	}
	
	public void setAmount(Double amount)
	{
		model.setAmount(amount);
	}
	
	@NotNull(message = "La date de début est obligatoire")
	@PastOrPresent(message = "La date de début ne peut pas être dans le futur")
	public LocalDateTime getStartDate()
	{
		return model.getStartDate();
	}
	
	public void setStartDate(LocalDateTime startDate)
	{
		model.setStartDate(startDate);
	}
	
	public LocalDateTime getEndDate()
	{
		return model.getEndDate();
	}
	
	public void setEndDate(LocalDateTime endDate)
	{
		model.setEndDate(endDate);
	}
	
	public int getVersion() 
	{
		return model.getVersion();
	}
	
	public LocalDateTime getModifDate() 
	{
		return model.getModifDate();
	}
                  
	public LocalDateTime getCreationDate() 
	{
		return model.getCreationDate();
	}
	
	public long getModifSellerId()
	{
		if (model.getModifSeller() != null)
		{
			return model.getModifSeller().getId();
		}
		return 0;
	}
	
	public void setModifSellerId(long modifSellerId)
	{
		if (model.getModifSeller() == null || model.getModifSeller().getId() != modifSellerId)
		{
			Seller modifSeller = new Seller();
			modifSeller.setId(modifSellerId);
			model.setModifSeller(modifSeller);
		}
	}

	public long getCreateSellerId()
	{
		if (model.getCreateSeller() != null)
		{
			return model.getCreateSeller().getId();
		}
		return 0;
	}
	
	@JsonIgnore
    public Price getModel()
    {
    	return model;
    }
	
    public void setModel(Price model)
    {
    	this.model = model;
    }
}
