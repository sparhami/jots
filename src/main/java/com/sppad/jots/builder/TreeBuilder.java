package com.sppad.jots.builder;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class TreeBuilder
{
  private static final int[] NO_PREFIX = new int[0];

  public static TreeBuilder from(final Object obj)
  {
    return new TreeBuilder(obj);
  }

  private ExclusionStrategy exclude = null;

  private InclusionStrategy include = null;

  private final Object obj;

  private String parentName = "unknown";

  private int[] prefix = null;

  private SetStrategy setStrategy = null;

  private TreeBuilder(final Object obj)
  {
    this.obj = obj;
  }

  public void build()
  {
    // TODO - create tree
  }

  public TreeBuilder exclusionStrategy(final ExclusionStrategy exclusionStrategy)
  {
    checkState(exclusionStrategy == null, "ExclusionStrategy was already set");
    this.exclude = checkNotNull(exclusionStrategy);

    return this;
  }

  public ExclusionStrategy getExclusionStrategy()
  {
    return firstNonNull(exclude, ExclusionStrategy.TRANSIENT_AND_ANNOTATED);
  }

  public InclusionStrategy getInclusionStrategy()
  {
    return firstNonNull(include, InclusionStrategy.FINAL_AND_ANNOTATED);
  }

  public String getParentName()
  {
    return firstNonNull(parentName, "Unknown");
  }

  public int[] getPrefix()
  {
    return firstNonNull(prefix, NO_PREFIX);
  }

  public SetStrategy getSetStrategy()
  {
    return firstNonNull(setStrategy, SetStrategy.SETTERS_AND_ANNOTATED);
  }

  public TreeBuilder inclusionStrategy(final InclusionStrategy inclusionStrategy)
  {
    checkState(inclusionStrategy == null, "InclusionStrategy was already set");
    this.include = checkNotNull(inclusionStrategy);

    return this;
  }

  public TreeBuilder parentName(final String parentName)
  {
    checkState(parentName == null, "ParentName was already set");
    this.parentName = checkNotNull(parentName);

    return this;
  }

  public TreeBuilder prefix(final int[] prefix)
  {
    checkState(prefix == null, "Prefix was already set");
    this.prefix = checkNotNull(prefix);

    return this;
  }

  public TreeBuilder setStrategy(final SetStrategy setStrategy)
  {
    checkState(setStrategy == null, "SetStrategy was already set");
    this.setStrategy = checkNotNull(setStrategy);

    return this;
  }

}
