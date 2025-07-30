package com.arem.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Iterables 
{
	
	public static <T> List<T> toList(Iterable<T> iterable)
	{
		if (iterable == null) { return null; }
		ArrayList<T> result = new ArrayList<T>();
		for (T x : iterable)
		{
			result.add(x);
		}
		return result;
	}
	
	public static <T> int size(Iterable<T> iterable)
	{
		if (iterable == null) { return 0; }
		int size = 0;
		for (@SuppressWarnings("unused") T x : iterable)
		{
			size = size + 1;
		}
		return size;
	}
	
	public static <T> Boolean isNullOrEmpty(Iterable<T> iterable)
	{
		if (iterable == null) {return true;}
		if (iterable.iterator().hasNext())
		{
			return false;
		}
		return true;
	}
	
	public static <T> Boolean isEmpty(Iterable<T> iterable)
	{
		if (iterable == null) {return false;}
		if (iterable.iterator().hasNext())
		{
			return false;
		}
		return true;
	}
	
	public static <T> List<T> findAll(Collection<T> iterable, Predicate<T> predicate)
	{
		if (isNullOrEmpty(iterable)) {return new ArrayList<T>();}
		return iterable.stream().filter(predicate).collect(Collectors.toList());
	}

	public static <T> T first(Collection<T> iterable, Predicate<T> predicate)
	{
		if (iterable == null) {return null;}
		for (T x : iterable)
		{
			if (predicate.test(x))
			{
				return x;
			}
		}
		return null;
	}

	public static <T> T single(Collection<T> iterable, Predicate<T> predicate)
	{
		List<T> result = findAll(iterable, predicate);		
		if (result.size() == 1)
		{
			return result.get(0);
		}
		if (result.size() == 0) 
		{
			throw new IllegalArgumentException("no element found");
		}
		else
		{
			throw new IllegalArgumentException("mors than one element found");
		}
	}
	
	public static <T> T findOne(Collection<T> iterable, Predicate<T> predicate)
	{
		List<T> result = findAll(iterable, predicate);
		if (result.size() == 1)
		{
			return result.get(0);
		}
		if (result.size() == 0) 
		{
			return null;
		}
		else
		{
			throw new IllegalArgumentException("mors than one element found");
		}
	}
}
