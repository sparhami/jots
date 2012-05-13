package com.sppad.common.data;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class ContextualCacher<K, V, C>
{
    public static interface ContextualCacheMissGetter<K, V, C>
    {
	V getValue(K key, C context);
    }

    private final Map<K, V> cache;

    private final ContextualCacheMissGetter<K, V, C> getter;

    public ContextualCacher(ContextualCacheMissGetter<K, V, C> getter)
    {
	this(getter, new HashMap<K, V>());
    }

    public ContextualCacher(ContextualCacheMissGetter<K, V, C> getter, Map<K, V> backingMap)
    {
	Preconditions.checkNotNull(getter);
	Preconditions.checkNotNull(backingMap);
	
	this.getter = getter;
	cache = backingMap;
    }

    public V get(K key, C context)
    {
	V value = cache.get(key);
	if (value == null)
	    cache.put(key, (value = getter.getValue(key, context)));

	return value;
    }
}
