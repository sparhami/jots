package com.sppad.common.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Clone
{
    @SuppressWarnings("unchecked")
    public static <V extends CloneableObject> HashMap<Integer, V> cloneIntegerValueHashMap(
	    final Map<Integer, V> target)
    {
	HashMap<Integer, V> result = new HashMap<Integer, V>();
	for (final Entry<Integer, V> entry : target.entrySet())
	    result.put(entry.getKey(), (V) entry.getValue().clone2());

	return result;
    }

    @SuppressWarnings("unchecked")
    public static <V extends CloneableObject> TreeMap<Integer, V> cloneIntegerValueTreeMap(
	    final Map<Integer, V> target)
    {
	TreeMap<Integer, V> result = new TreeMap<Integer, V>();
	for (final Entry<Integer, V> entry : target.entrySet())
	    result.put(entry.getKey(), (V) entry.getValue().clone2());

	return result;
    }

    @SuppressWarnings("unchecked")
    public static <V extends CloneableObject> HashMap<String, V> cloneStringValueHashMap(
	    final Map<String, V> target)
    {
	HashMap<String, V> result = new HashMap<String, V>();
	for (final Entry<String, V> entry : target.entrySet())
	    result.put(entry.getKey(), (V) entry.getValue().clone2());

	return result;
    }

    @SuppressWarnings("unchecked")
    public static <V extends CloneableObject> TreeMap<String, V> cloneStringValueTreeMap(
	    final Map<String, V> target)
    {
	TreeMap<String, V> result = new TreeMap<String, V>();
	for (final Entry<String, V> entry : target.entrySet())
	    result.put(entry.getKey(), (V) entry.getValue().clone2());

	return result;
    }

    @SuppressWarnings("unchecked")
    public static <K extends CloneableObject, V extends CloneableObject> HashMap<K, V> cloneToHashMap(
	    final Map<K, V> target)
    {
	HashMap<K, V> result = new HashMap<K, V>();
	for (final Map.Entry<K, V> entry : target.entrySet())
	    result.put((K) entry.getKey().clone2(), (V) entry.getValue().clone2());

	return result;
    }

    @SuppressWarnings("unchecked")
    public static <K extends CloneableObject, V extends CloneableObject> TreeMap<K, V> cloneToTreeMap(
	    final Map<K, V> target)
    {
	TreeMap<K, V> result = new TreeMap<K, V>();
	for (final Map.Entry<K, V> entry : target.entrySet())
	    result.put((K) entry.getKey().clone2(), (V) entry.getValue().clone2());

	return result;
    }
}
