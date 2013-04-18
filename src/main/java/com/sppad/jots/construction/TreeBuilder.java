package com.sppad.jots.construction;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.sppad.jots.constructor.SnmpTree;

public class TreeBuilder
{
  private static final int[] NO_PREFIX = new int[0];

  public static TreeBuilder from(final Object obj)
  {
    return new TreeBuilder(obj);
  }

  private InclusionStrategy inclusionStrategy = null;

  private final Object obj;

  private String parentName = "unknown";

  private int[] prefix = null;

  private SetStrategy setStrategy = null;

  private TreeBuilder(final Object obj)
  {
    this.obj = obj;
  }

  public SnmpTree build()
  {
    return TreeConstructor.create(obj, this);
  }

  InclusionStrategy getInclusionStrategy()
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
    return firstNonNull(setStrategy, SetStrategy.SETTERS_AND_ANNOTATED);
  }

  public TreeBuilder inclusionStrategy(final InclusionStrategy strategy)
  {
    checkState(this.inclusionStrategy == null,
        "InclusionStrategy was already set");
    this.inclusionStrategy = checkNotNull(strategy);

    return this;
  }

  public TreeBuilder parentName(final String parentName)
  {
    checkState(this.parentName == null, "ParentName was already set");
    this.parentName = checkNotNull(parentName);

    return this;
  }

  public TreeBuilder prefix(final int[] prefix)
  {
    checkState(this.prefix == null, "Prefix was already set");
    this.prefix = checkNotNull(prefix);

    return this;
  }

  public TreeBuilder setStrategy(final SetStrategy setStrategy)
  {
    checkState(this.setStrategy == null, "SetStrategy was already set");
    this.setStrategy = checkNotNull(setStrategy);

    return this;
  }

}
