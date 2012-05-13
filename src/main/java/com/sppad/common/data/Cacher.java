package com.sppad.common.data;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class Cacher<K, V>
{
    public static interface CacheMissGetter<K, V>
    {
	V getValue(K key);
    }

    private final Map<K, V> cache;

    private final CacheMissGetter<K, V> getter;

    public Cacher(CacheMissGetter<K, V> getter)
    {
	this(getter, new HashMap<K, V>());
    }

    public Cacher(CacheMissGetter<K, V> getter, Map<K, V> backingMap)
    {
	Preconditions.checkNotNull(getter);
	Preconditions.checkNotNull(backingMap);
	
	this.getter = getter;
	this.cache = backingMap;
    }

    public V get(K key)
    {
	V value = cache.get(key);
	if (value == null)
	    cache.put(key, (value = getter.getValue(key)));

	return value;
    }
}
