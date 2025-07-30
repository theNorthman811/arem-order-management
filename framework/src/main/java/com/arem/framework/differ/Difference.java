package com.arem.framework.differ;

public class Difference
{
	
	private String prepertyName;
	private String oldValue;
	private String newValue;
	
	
	public Difference(String prepertyName, String oldValue, String newValue)
	{
		super();
		this.prepertyName = prepertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getPrepertyName() 
	{
		return prepertyName;
	}
	
	public String getOldValue() 
	{
		return oldValue;
	}
	
	public String getNewValue()
	{
		return newValue;
	}

}
