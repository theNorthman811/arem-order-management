package com.arem.framework;

public final class StringHelper
{

	public static Boolean isNullOrEmpty(String str)
	{
		return str == null || str.isEmpty();
	}
	
	public static Boolean isNotNullOrEmpty(String str)
	{
		return str != null && !str.isEmpty();
	}
	
}
