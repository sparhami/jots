package com.sppad.jots;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;

import javax.annotation.Nullable;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.sppad.jots.exceptions.SnmpNotWritableException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;
import com.sppad.jots.lookup.LookupEntry;
import com.sppad.jots.lookup.SnmpLookupField;
import com.sppad.jots.util.SnmpUtils;

/**
 * Contains fields in an SNMP tree, providing for getting/setting tree entries.
 * 
 * @see SnmpTreeBuilder
 */
public class SnmpTree implements Iterable<VariableBinding>
{
	/** The default maximum number of cached OID index entries */
	private static final int DEFAULT_INDEX_CACHE_SIZE = 100;

	/** Maps a SnmpLookupField to a VariableBinding */
	private static final Function<SnmpLookupField, VariableBinding> LOOKUP_FIELD_TO_VARBIND = new Function<SnmpLookupField, VariableBinding>() {
		@Override
		public VariableBinding apply(final SnmpLookupField arg)
		{
			return arg.toVarBind();
		}
	};

	/** A sorted array that stores all the items in the tree */
	final SnmpLookupField[] fieldArray;

	/** Used to cache OID lookups */
	LoadingCache<OID, Integer> indexCacher;

	/**
	 * Stores the size of the index cache, so that it can be persisted when
	 * merging with another SnmpTree
	 */
	int indexCacheSize;

	/** The last valid index */
	final int lastIndex;

	/** The prefix used to create the OIDs in the tree */
	final int[] prefix;

	/**
	 * @param prefix
	 *            An int array that all OIDs in the tree have in common
	 * @param snmpFields
	 *            A sorted set of SnmpLookupFields that make up the tree
	 */
	public SnmpTree(final int[] prefix,
			final SortedSet<SnmpLookupField> snmpFields)
	{
		this(prefix, snmpFields, DEFAULT_INDEX_CACHE_SIZE);
	}

	public SnmpTree(final int[] prefix,
			final SortedSet<SnmpLookupField> fields, final int cacheSize)
	{
		this.prefix = prefix;
		this.lastIndex = fields.size() - 1;
		this.fieldArray = fields.toArray(new SnmpLookupField[fields.size()]);

		setCacheSize(cacheSize);
	}

	/**
	 * 
	 * @param index
	 *            The index in the tree to get the VariableBinding for
	 * @return A VariableBinding containing the OID and current value for the
	 *         given index
	 */
	public VariableBinding get(final int index)
	{
		return fieldArray[index].toVarBind();
	}

	/**
	 * Performs a get, returning the value for the field corresponding to a
	 * given OID.
	 * 
	 * @param oid
	 *            The OID to get the value for
	 * @return A VariableBinding containing the requested OID and the associated
	 *         value
	 * @throws SnmpOidNotFoundException
	 */
	public VariableBinding get(final OID oid) throws SnmpOidNotFoundException
	{
		return _get(checkNotNull(oid));
	}

	/**
	 * Gets the annotation for the source field represented by the given index.
	 * 
	 * @param index
	 *            The index of the field
	 * @param annotationClass
	 *            The annotation class to get.
	 * 
	 * @return The annotation if it exists, null otherwise.
	 */
	public Annotation getAnnotation(final int index,
			@Nullable final Class<? extends Annotation> annotationClass)
	{
		return fieldArray[index].getAnnotation(annotationClass);
	}

	/**
	 * @return The last index in the tree
	 */
	public int getLastIndex()
	{
		return lastIndex;
	}

	/**
	 * Gets VariableBinding for the following OID. The internal OID index cache
	 * is not used/updated since SNMP walks would invalidate the entire cache.
	 * 
	 * @param oid
	 *            A reference OID
	 * @return A VariableBinding containing the current value of the OID
	 *         following <i>oid</i>
	 * @throws SnmpOidNotFoundException
	 */
	public VariableBinding getNext(final OID oid)
			throws SnmpOidNotFoundException
	{
		return _getNext(checkNotNull(oid));
	}

	/**
	 * Gets the index for the following OID. The internal OID index cache is not
	 * used/updated since SNMP walks would invalidate the entire cache.
	 * 
	 * @param oid
	 *            A reference OID
	 * @return The index for the next OID.
	 */
	public int getNextIndex(final OID oid)
	{
		return _getNextIndex(checkNotNull(oid));
	}

	/**
	 * Returns an iterator that allows iterating through the tree to get
	 * VariableBindings, which are created at access time.
	 * 
	 * @return An Iterator of VariableBindings
	 */
	@Override
	public Iterator<VariableBinding> iterator()
	{
		return FluentIterable //
				.from(Arrays.asList(fieldArray)) //
				.transform(LOOKUP_FIELD_TO_VARBIND) //
				.iterator();
	}

	/**
	 * Merges two SnmpTrees into 1 tree. If both trees contain the same OID,
	 * then the OID is represented by the index from the passed in tree and not
	 * the current object.
	 * 
	 * @param other
	 *            The tree to merge in
	 * @return An SnmpTree with fields from both the current object and tree
	 */
	public SnmpTree mergeSnmpTrees(final SnmpTree other)
	{
		checkNotNull(other);

		final SortedSet<SnmpLookupField> mergedFields = Sets.newTreeSet();

		mergedFields.addAll(Arrays.asList(fieldArray));
		mergedFields.addAll(Arrays.asList(other.fieldArray));

		final int[] mergedPrefix = SnmpUtils.commonPrefix(prefix, other.prefix);
		final int cacherSize = Math.max(indexCacheSize, other.indexCacheSize);

		return new SnmpTree(mergedPrefix, mergedFields, cacherSize);
	}

	/**
	 * Performs a set for the given OID.
	 * 
	 * @param oid
	 *            The OID to set
	 * @param value
	 *            The value to set
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpNotWritableException
	 */
	public void set(final OID oid, final String value)
			throws SnmpNotWritableException, SnmpOidNotFoundException
	{
		_set(checkNotNull(oid), checkNotNull(value), true);
	}

	/**
	 * Performs a set for the given OID.
	 * 
	 * @param oid
	 *            The OID to set
	 * @param value
	 *            The value to set
	 * @param checkWritable
	 *            Whether or not to check of the field is writable or force
	 *            setting the value
	 * @throws SnmpNotWritableException
	 * @throws SnmpOidNotFoundException
	 */
	public void set(final OID oid, final String value,
			final boolean checkWritable) throws SnmpNotWritableException,
			SnmpOidNotFoundException
	{
		_set(checkNotNull(oid), checkNotNull(value), checkWritable);
	}

	/**
	 * Sets the cache size for caching indices for OID lookups. The OIDs cached
	 * do not necessarily have to reside in the tree. This causes all previously
	 * cached items to be discarded.
	 * 
	 * @param maximumSize
	 *            The maximum number of entries to cache
	 */
	public void setCacheSize(final int maximumSize)
	{
		checkArgument(maximumSize >= 0, "maximumSize must not be negative");

		indexCacheSize = maximumSize;
		indexCacher = CacheBuilder.newBuilder() //
				.maximumSize(indexCacheSize) //
				.build(new CacheLoader<OID, Integer>() {
					@Override
					public Integer load(final OID key)
					{
						return getIndex(key);
					}
				});
	}

	private VariableBinding _get(final OID oid) throws SnmpOidNotFoundException
	{
		try
		{
			return get(getIndexCached(oid));
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new SnmpOidNotFoundException(oid);
		}
	}

	private VariableBinding _getNext(final OID oid)
			throws SnmpOidNotFoundException
	{
		try
		{
			return get(getNextIndex(oid));
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new SnmpOidNotFoundException(oid);
		}
	}

	private int _getNextIndex(final OID oid)
	{
		return Math.abs(getIndex(oid) + 1);
	}

	private void _set(final OID oid, final String value,
			final boolean checkWritable) throws SnmpNotWritableException,
			SnmpOidNotFoundException
	{
		try
		{
			final SnmpLookupField field = fieldArray[getIndexCached(oid)];
			if (checkWritable && !field.isWritable())
				throw new SnmpNotWritableException(oid);

			field.set(value);
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new SnmpOidNotFoundException(oid);
		}
	}

	/**
	 * Performs a binary search to get the OID index.
	 * 
	 * @param oid
	 *            The OID to lookup
	 * @return The index of the OID in the fieldArray if it exists, or -(index
	 *         of next OID) if it doesn't
	 */
	private int getIndex(final OID oid)
	{
		return Arrays.binarySearch(fieldArray, new LookupEntry(oid));
	}

	/**
	 * A wrapper around the index cacher.
	 * 
	 * @param oid
	 *            The OID to lookup
	 * @return The index, as returned by {@link #getIndex}
	 */
	private int getIndexCached(final OID oid)
	{
		return indexCacher.getUnchecked(oid);
	}
}
