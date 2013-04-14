package com.sppad.jots.constructor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.sppad.jots.exceptions.SnmpNoMoreEntriesException;
import com.sppad.jots.exceptions.SnmpNotWritableException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;
import com.sppad.jots.exceptions.SnmpPastEndOfTreeException;
import com.sppad.jots.lookup.SnmpLookupField;
import com.sppad.jots.util.SnmpUtils;

/**
 * 
 * 
 * @author sepand
 * @see SnmpTreeConstructor
 */
public class SnmpTree implements Iterable<VariableBinding>
{
  /** The default maximum number of cached OID index entries */
  private static final int DEFAULT_INDEX_CACHE_SIZE = 100;

  /** Maps a SnmpLookupField to a VariableBinding */
  private static final Function<SnmpLookupField, VariableBinding> LOOKUP_FIELD_TO_VARBIND = new Function<SnmpLookupField, VariableBinding>()
  {
    @Override
    public VariableBinding apply(final SnmpLookupField arg)
    {
      return createVarBind(arg.getOid(), arg.getValue());
    }
  };

  /** The last valid index */
  public final int lastIndex;

  /** A sorted array that stores all the items in the tree */
  protected SnmpLookupField[] fieldArray;

  /** Used to cache OID lookups */
  protected LoadingCache<OID, Integer> indexCacher;

  /**
   * Stores the size of the index cache, so that it can be persisted when
   * merging with another SnmpTree
   */
  protected int indexCacheSize;

  /** The prefix used to create the OIDs in the tree */
  protected final int[] prefix;

  /** Creates a VariableBinding object for returning OID, value pairs */
  protected static VariableBinding createVarBind(
      final OID oid,
      final Object object)
  {
    final Variable variable;
    if (object instanceof Integer)
      variable = new Integer32((Integer) object);
    else
      variable = new OctetString(object == null ? "" : object.toString());

    return new VariableBinding(oid, variable);
  }

  protected SnmpTree(final int[] prefix, final SnmpLookupField[] fieldArray)
  {
    this(prefix, fieldArray, DEFAULT_INDEX_CACHE_SIZE);
  }

  protected SnmpTree(
      final int[] prefix,
      final SnmpLookupField[] fieldArray,
      final int cacheSize)
  {
    this.prefix = prefix;
    this.lastIndex = fieldArray.length - 1;
    this.fieldArray = fieldArray;
    this.indexCacher = createCacher(cacheSize);
  }

  protected SnmpTree(
      final int[] prefix,
      final SortedSet<SnmpLookupField> snmpFields)
  {
    this(prefix, snmpFields.toArray(new SnmpLookupField[snmpFields.size()]));
  }

  public VariableBinding get(final int index)
  {
    final SnmpLookupField field = getFieldWithBoundsChecking(index);
    return createVarBind(field.getOid(), field.getValue());
  }

  public VariableBinding get(final OID oid)
  {
    return get(getCachedIndex(oid));
  }

  /**
   * Gets the annotation for the source field represented by the given index.
   * 
   * @param index
   *          The index of the field
   * @param annotationClass
   *          The annotation class to get.
   * 
   * @return The annotation if it exists, null otherwise.
   */
  public Annotation getAnnotation(
      final int index,
      final Class<?> annotationClass)
  {
    return getFieldWithBoundsChecking(index).getAnnotation(annotationClass);
  }

  /**
   * Gets VariableBinding for the following OID. The internal OID index cache is
   * not used/updated since SNMP walks would invalidate the entire cache.
   * 
   * @param oid
   *          A reference OID
   * @return A VariableBinding containing the current value of the OID following
   *         <b>oid</b>
   */
  public VariableBinding getNext(final OID oid)
  {
    return get(getNextIndex(oid));
  }

  /**
   * Gets the index for the following OID. The internal OID index cache is not
   * used/updated since SNMP walks would invalidate the entire cache.
   * 
   * @param oid
   *          A reference OID
   * @return The index for the next OID.
   */
  public int getNextIndex(final OID oid)
  {
    Preconditions.checkNotNull(oid);

    final int index = Math.abs(getIndex(oid) + 1);
    if (index > lastIndex)
      throw new SnmpPastEndOfTreeException(
          "There are no more values in this MIB");

    return index;
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
   * Merges two SnmpTrees into 1 tree. If both trees contain the same OID, then
   * the OID is represented by the index from tree and not the current object.
   * 
   * @param other
   *          The tree to merge in
   * @return An SnmpTree with fields from both the current object and tree
   */
  public SnmpTree mergeSnmpTrees(final SnmpTree other)
  {
    Preconditions.checkNotNull(other, "argument must not be null");

    final List<SnmpLookupField> fields = new ArrayList<SnmpLookupField>(
        this.lastIndex);

    int thisIndex = 0; // index in this
    int otherIndex = 0; // index in other

    while (thisIndex <= this.lastIndex && otherIndex <= other.lastIndex)
    {
      SnmpLookupField thisField = this.fieldArray[thisIndex];
      SnmpLookupField otherField = other.fieldArray[otherIndex];

      // Ties go to the field from other
      int cmp = otherField.compareTo(thisField);
      fields.add(cmp <= 0 ? otherField : thisField);

      if (cmp <= 0)
        otherIndex++;

      if (cmp >= 0)
        thisIndex++;
    }

    // copy any left over fields (should all be greater now)
    for (int i = otherIndex; i <= other.lastIndex; i++)
      fields.add(other.fieldArray[i]);
    for (int i = thisIndex; i <= this.lastIndex; i++)
      fields.add(this.fieldArray[i]);

    int[] prefix = SnmpUtils.findCommonPrefix(this.prefix, other.prefix);
    SnmpLookupField[] fieldArray = fields.toArray(new SnmpLookupField[fields
        .size()]);
    int cacherSize = Math.max(this.indexCacheSize, other.indexCacheSize);

    return new SnmpTree(prefix, fieldArray, cacherSize);
  }

  /**
   * Performs a set for the given OID.
   * 
   * @param oid
   * @param value
   */
  public void set(final OID oid, final String value)
  {
    set(oid, value, true);
  }

  /**
   * Performs a set for the given OID.
   * 
   * @param oid
   *          The OID to set
   * @param value
   *          The value to set
   * @param checkWritable
   *          Whether or not to check of the field is writable or force setting
   *          the value
   */
  public void set(final OID oid, final String value, final boolean checkWritable)
  {
    Preconditions.checkNotNull(oid);

    SnmpLookupField field = getFieldWithBoundsChecking(getCachedIndex(oid));
    if (checkWritable && !field.isWritable())
      throw new SnmpNotWritableException("Cannot write to this OID");

    field.set(value);
  }

  /**
   * Sets the cache size for caching indices for OID lookups. The OIDs cached do
   * not necessarily have to reside in the tree. This causes all previously
   * cached items to be discarded.
   * 
   * @param size
   *          The maximum number of entries to cache
   */
  public void setCacheSize(final int size)
  {
    indexCacher = createCacher(size);
  }

  /**
   * Creates the internal index cacher.
   * 
   * @param size
   *          The maximum number of entries to cache
   * @return The cacher used for index lookups
   */
  private LoadingCache<OID, Integer> createCacher(int size)
  {
    return CacheBuilder.newBuilder() //
        .maximumSize(indexCacheSize = size) //
        .build(new CacheLoader<OID, Integer>()
        {
          @Override
          public Integer load(final OID key)
          {
            return getIndex(key);
          }
        });
  }

  private SnmpLookupField getFieldWithBoundsChecking(final int index)
  {
    if (index < 0)
      throw new SnmpOidNotFoundException("Oid not in table");
    if (index > lastIndex)
      throw new SnmpNoMoreEntriesException(
          "There are no more values in this MIB");

    return fieldArray[index];
  }

  /**
   * A wrapper around the index cacher.
   * 
   * @param oid
   *          The OID to lookup
   * @return The index, as returned by {@link #getIndex}
   */
  private int getCachedIndex(final OID oid)
  {
    try
    {
      return indexCacher.get(oid);
    }
    catch (ExecutionException e)
    {
      throw Throwables.propagate(e.getCause());
    }
  }

  /**
   * Performs a binary search to get the oid index, if it exists. Code from
   * {@link Arrays#binarySearch(Object[], Object, java.util.Comparator)}. Don't
   * want to create a SnmpLookupField to perform a binary search, so duplicating
   * the code here.
   * 
   * @param oid
   *          The OID to lookup
   * @return The index of the oid in the fieldArray if it exists, or -(index of
   *         next OID) if it doesn't
   */
  private int getIndex(final OID oid)
  {
    int low = 0;
    int high = fieldArray.length - 1;

    while (low <= high)
    {
      final int mid = (low + high) >>> 1;

      int compare = fieldArray[mid].getOid().compareTo(oid);
      if (compare < 0)
        low = mid + 1;
      else if (compare > 0)
        high = mid - 1;
      else
        return mid; // key found
    }
    return -(low + 1); // key not found.
  }
}
