package com.arem.framework.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.arem.core.model.ICachable;
import com.arem.framework.Iterables;


@Component
public abstract class Cache<T extends ICachable> 
{
	
	protected HashMap<Long, T> cache;
	protected HashMap<Long, List<T>> cacheByGroup;
	private final Object sync = new Object();
	private Boolean enabled;
	private Boolean orderByGroup;
	
	protected Cache()
	{
		cache = new HashMap<Long, T>();
		orderByGroup = false;
		enabled = false;
	}
	
	protected Cache(Boolean orderByGroup)
	{
		this.orderByGroup = orderByGroup;
		enabled = false;
		if (orderByGroup == false)
		{
			cache = new HashMap<Long, T>();
		}
		else
		{
			cacheByGroup = new HashMap<>();
		}
	}
	
	public void putAll(Iterable<T> all)
	{
		if (!enabled)
		{
			synchronized(sync)
			{
				if (!enabled)
				{
					enabled = true;
					if (orderByGroup)
					{
						for(T object : all)
						{
							if (cacheByGroup.containsKey(object.getGroupId()))
							{
								List<T> objects = cacheByGroup.get(object.getGroupId());
								objects.add(object);
							}
							else
							{
								List<T> objects = new ArrayList<T>();
								objects.add(object);
								cacheByGroup.put(object.getGroupId(), objects);
							}
						}
					}
					else
					{
						all.forEach(x -> cache.put(x.getId(), x));
					}
				}
			}
		}
	}
	
	public void save(Iterable<T> objects) throws Exception
	{
		for(T object : objects)
		{
			save(object);
		}
	}
	
	public void save(T object)
	{
		synchronized(sync)
		{
			if (orderByGroup)
			{
				if (cacheByGroup.containsKey(object.getGroupId()))
				{
					List<T> objects = cacheByGroup.get(object.getGroupId());
					T old = Iterables.findOne(objects, x -> x.getId() == object.getId());
					if (old == null)
					{
						objects.add(object);
					}
					else
					{
						objects.remove(old);
						objects.add(object);
					}
				}
				else
				{
					List<T> objects = new ArrayList<T>();
					objects.add(object);
					cacheByGroup.put(object.getGroupId(), objects);
				}
			}
			else
			{
				cache.put(object.getId(), object);
			}
		}
	}
	
	public void delete(T object) throws Exception
	{
		synchronized(sync)
		{
			if (orderByGroup)
			{
				if (cacheByGroup.containsKey(object.getGroupId()))
				{
					List<T> objects = cacheByGroup.get(object.getGroupId());
					T old = Iterables.findOne(objects, x -> x.getId() == object.getId());
					if (old != null)
					{
						objects.remove(old);
					}
				}
			}
			else
			{
				cache.remove(object.getId());
			}
		}
	}
	
	public void delete(Iterable<T> objects) throws Exception
	{
		for(T object : objects)
		{
			delete(object);
		}
	}
	
	public void disable()
	{
		if (cache != null)
		{
			cache.clear();
		}
		if (cacheByGroup != null)
		{
			cacheByGroup.clear();
		}
		enabled = false;
	}
	
	public Boolean isEnabled()
	{
		return enabled;
	}
	
	public List<T> findAll()
	{
		if (orderByGroup)
		{
			Collection<List<T>> collection = cacheByGroup.values();
			List<T> result = new ArrayList<T>();
			collection.forEach(x -> result.addAll(x));
			return result;
		}
		else
		{
			Collection<T> collection = cache.values();
			List<T> result = new ArrayList<T>();
			collection.forEach(x -> result.add(x));
			return result;
		}
	}
	
	public List<T> findAll(Predicate<T> predicate)
	{
		Collection<T> list = cache.values();
		return Iterables.findAll(list, predicate);
	}
	
	public T single(Predicate<T> predicate)
	{
		Collection<T> list = cache.values();
		return Iterables.single(list, predicate);
	}
	
	public T first(Predicate<T> predicate)
	{
		Collection<T> list = cache.values();
		return Iterables.first(list, predicate);
	}
	
	public T findOne(Predicate<T> predicate)
	{
		Collection<T> list = cache.values();
		return Iterables.findOne(list, predicate);
	}
	
	public T findById(long id)
	{
		return cache.get(id);
	}
}
