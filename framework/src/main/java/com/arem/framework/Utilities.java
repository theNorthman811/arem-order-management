package com.arem.framework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities
{

	public static boolean match(String field, String regex, int minLength, int maxLength)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(field);
		if (matcher.matches())
		{
			return field.length() >= minLength && field.length() <= maxLength;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean match(String field, String regex)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(field);
		return matcher.matches();
	}
}
