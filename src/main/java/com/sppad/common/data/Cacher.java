package com.sppad.common.data;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * Used for caching data for a given key. When there does not exist a mapping
 * for a given key, a user-supplied function is used to get the value as well as
 * update the cache.
 * 
 * @param <K>
 *          The key type
 * @param <V>
 *          The value type
 */
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
