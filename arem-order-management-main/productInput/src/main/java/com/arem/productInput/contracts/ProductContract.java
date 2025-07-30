package com.arem.productInput.contracts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.arem.core.model.Measure;
import com.arem.core.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductContract
{
	private Product model;
	
	private List<PriceContract> prices;
	
	public ProductContract()
	{
		model = new Product();
		prices = new ArrayList<>();
	}
	
	public ProductContract(Product model)
	{
		this.model = model;
		prices = new ArrayList<>();
	}
	
	public long getId()
	{
		return model.getId();
	}
	
	public void setId(long id)
	{
		model.setId(id);
	}
	
	@NotBlank(message = "Le nom est obligatoire")
	@Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
	public String getName()
	{
		return model.getName();
	}
	
	public void setName(String name)
	{
		model.setName(name);
	}
	
	@Size(max = 50, message = "La marque ne peut pas dépasser 50 caractères")
	public String getMarque() 
	{
		return this.model.getMarque();
	}

	public void setMarque(String marque)
	{
		this.model.setMarque(marque);
	}
	
	@NotBlank(message = "La référence est obligatoire")
	@Size(max = 50, message = "La référence ne peut pas dépasser 50 caractères")
	public String getReference()
	{
		return model.getReference();
	}
	
	public void setReference(String reference)
	{
		model.setReference(reference);
	}
	
	@Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
	public String getDescription()
	{
		return model.getDescription();
	}
	
	public void setDescription(String description)
	{
		model.setDescription(description);
	}
	
	@Size(max = 255, message = "Le commentaire ne peut pas dépasser 255 caractères")
	public String getComment()
	{
		return model.getComment();
	}
	
	public void setComment(String comment)
	{
		model.setComment(comment);
	}
	
	public long getModifSellerId()
	{
		if (model.getModifSeller() != null)
		{
			return model.getModifSeller().getId();
		}
		return 0;
	}
	
	public long getCreateSellerId()
	{
		if (model.getCreateSeller() != null)
		{
			return model.getCreateSeller().getId();
		}
		return 0;
	}

	public int getVersion() 
	{
		return model.getVersion();
	}
	
	public void setVersion(int version) 
	{
		model.setVersion(version);
	}
	
	public LocalDateTime getModifDate() 
	{
		return model.getModifDate();
	}
                  
	public LocalDateTime getCreationDate() 
	{
		return model.getCreationDate();
	}
	
	@NotNull(message = "La quantité est obligatoire")
	@Min(value = 0, message = "La quantité doit être positive")
	public double getQuantity() 
	{
		return this.model.getQuantity();
	}

	public void setQuantity(double quantity) 
	{
		this.model.setQuantity(quantity);
	}
	
	@NotNull(message = "L'unité de mesure est obligatoire")
	public Measure getMeasure() 
	{
		return this.model.getMeasure();
	}

	public void setMeasure(Measure measure) 
	{
		this.model.setMeasure(measure);
	}
		
	public List<PriceContract> getPrices()
	{
		return prices;
	}
	
	public void setPrices(List<PriceContract> prices)
	{
		this.prices = prices;
	}
	
	@JsonIgnore
    public Product getModel()
    {
    	return model;
    }
	
    public void setModel(Product model)
    {
    	this.model = model;
    }
}
