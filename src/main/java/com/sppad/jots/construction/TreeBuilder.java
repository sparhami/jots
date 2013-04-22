package com.sppad.jots.construction;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Field;

import com.google.common.base.Predicate;
import com.sppad.jots.SnmpTree;
import com.sppad.jots.construction.config.SimpleInclusionStrategy;

public class TreeBuilder
{
  public enum SetStrategy
  {
    /** Any Field */
    ALL,

    /** Fields that are annotated */
    ANNOTATED_ONLY,

    /** Fields with a setter or that are annotated by {@link SnmpSettable} */
    SETTERS_OR_ANNOTATED,

    /** Fields with a setter and that are annotated */
    SETTERS_AND_ANNOTATED
  }

  private static final int[] NO_PREFIX = new int[0];

  /**
   * Creates an TreeBuilder, which can be used to construct an SnmpTree for a
   * Java Object.
   * 
   * @param obj
   *          The object to use as the root of the SnmpTree
   */
  public static TreeBuilder from(final Object obj)
  {
    return new TreeBuilder(obj);
  }

  private Predicate<Field> inclusionStrategy = null;

  private final Object obj;

  private String parentName = "unknown";

  private int[] prefix = null;

  private SetStrategy setStrategy = null;

  private TreeBuilder(final Object obj)
  {
    this.obj = obj;
  }

  /**
   * @return The SnmpTree for the given Object, using the given settings.
   */
  public SnmpTree build()
  {
    return TreeConstructor.create(obj, this);
  }

  Predicate<Field> getInclusionStrategy()
  {
    return firstNonNull(inclusionStrategy, new SimpleInclusionStrategy());
  }

  String getParentName()
  {
    return firstNonNull(parentName, "Unknown");
  }

  int[] getPrefix()
  {
    return firstNonNull(prefix, NO_PREFIX);
  }

  SetStrategy getSetStrategy()
  {
    return firstNonNull(setStrategy, SetStrategy.SETTERS_OR_ANNOTATED);
  }

  /**
   * Determines what fields should be included in the generated SnmpTree.
   * Defaults to {@link SimpleInclusionStrategy}.
   * 
   * @param strategy
   *          The InclusionStrategy to use
   */
  public TreeBuilder inclusionStrategy(final Predicate<Field> strategy)
  {
    checkState(this.inclusionStrategy == null,
        "InclusionStrategy was already set");
    this.inclusionStrategy = checkNotNull(strategy);

    return this;
  }

  /**
   * Sets the name for the parent of the root entry in the MIB.
   * 
   * @param parentName
   */
  public TreeBuilder parentName(final String parentName)
  {
    checkState(this.parentName == null, "ParentName was already set");
    this.parentName = checkNotNull(parentName);

    return this;
  }

  /**
   * Sets a prefix to use when generating the tree
   * 
   * @param prefix
   */
  public TreeBuilder prefix(final int[] prefix)
  {
    checkState(this.prefix == null, "Prefix was already set");
    this.prefix = checkNotNull(prefix);

    return this;
  }

  /**
   * Determines what fields should be allowed to be set through the resulting
   * SnmpTree. Defaults to {@link SetStrategy#SETTERS_OR_ANNOTATED}
   * 
   * @param setStrategy
   *          The SetStrategy to use
   */
  public TreeBuilder setStrategy(final SetStrategy setStrategy)
  {
    checkState(this.setStrategy == null, "SetStrategy was already set");
    this.setStrategy = checkNotNull(setStrategy);

    return this;
  }
}
