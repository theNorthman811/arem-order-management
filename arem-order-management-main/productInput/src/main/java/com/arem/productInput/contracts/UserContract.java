package com.arem.productInput.contracts;

import java.time.LocalDateTime;

import com.arem.core.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class UserContract<T extends User>
{
	protected T model;
	
	protected UserContract(T model)
	{
		this.model = model;
	}
	    
	public String getFirstName()
	{
		return model.getFirstName();
	}

	public void setFirstName(String firstName)
	{
		model.setFirstName(firstName);
	}

	public String getLastName()
	{
		return model.getLastName();
		
	}

	public void setLastName(String lastName)
	{
		model.setLastName(lastName);
	}

	public String getPickName()
	{
		return model.getPickName();
	}

	public void setPickName(String pickName)
	{
		model.setPickName(pickName);
	}

	public String getPhoneNumber() 
	{
		return model.getPhoneNumber();
	}

	public void setPhoneNumber(String phoneNumber)
	{
		model.setPhoneNumber(phoneNumber);
	}

	public String getAddress() 
	{
		return model.getAddress();
	}

	public void setAddress(String address) 
	{
		model.setAddress(address);
	}

	public long getId() 
	{
		return model.getId();
	}
	
	public void setId(long id) 
	{
		model.setId(id);
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
    
	@JsonIgnore
    public T getModel()
    {
    	return model;
    }
	
    public void setModel(T model)
    {
    	this.model = model;
    }
	
}
