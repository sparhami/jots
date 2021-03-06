package com.sppad.jots.construction;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.OutputStream;
import java.lang.reflect.Field;

import com.google.common.base.Predicate;
import com.sppad.jots.SnmpTree;
import com.sppad.jots.annotations.SnmpSettable;
import com.sppad.jots.construction.config.DefaultInclusionStrategy;
import com.sppad.jots.construction.config.SimpleInclusionStrategy;

/**
 * Used for building a {@link SnmpTree} or a MIB file corresponding to a
 * SnmpTree.
 */
public class SnmpTreeBuilder
{
	public enum SetStrategy
	{
		/** Any Field */
		ALL,

		/** Fields that are annotated */
		ANNOTATED_ONLY,

		/** Fields with a setter and that are annotated */
		SETTERS_AND_ANNOTATED,

		/** Fields with a setter or that are annotated by {@link SnmpSettable} */
		SETTERS_OR_ANNOTATED
	}

	private static final int[] NO_PREFIX = new int[0];

	/**
	 * Creates an TreeBuilder, which can be used to construct an SnmpTree for a
	 * Java Object.
	 * 
	 * @param obj
	 *            The object to use as the root of the SnmpTree
	 */
	public static SnmpTreeBuilder from(final Object obj)
	{
		return new SnmpTreeBuilder(obj);
	}

	/** Determines what Fields are included */
	private Predicate<Field> inclusionStrategy = null;

	/** The object to build an SnmpTree for */
	private final Object obj;

	/** The prefix OID for all elements in the tree */
	private int[] prefix = null;

	/** The strategy for considering items as writable */
	private SetStrategy setStrategy = null;

	private SnmpTreeBuilder(final Object obj)
	{
		this.obj = obj;
	}

	/**
	 * @return The SnmpTree for the given Object, using the given settings.
	 */
	public SnmpTree build()
	{
		return SnmpTreeConstructor.create(obj, this);
	}

	/**
	 * Builds a MIB, printing it to the supplied OutputStream.
	 */
	public void buildMib(final String mibName, final String rootName,
			final String parentName, final OutputStream os)
	{
		checkNotNull(mibName);
		checkNotNull(rootName);
		checkNotNull(parentName);
		checkNotNull(os);

		MibGenerator.generateMib(obj, this, mibName, rootName, parentName, os);
	}

	/**
	 * Determines what fields should be included in the generated SnmpTree.
	 * Defaults to {@link SimpleInclusionStrategy}.
	 * 
	 * @param strategy
	 *            The InclusionStrategy to use
	 */
	public SnmpTreeBuilder inclusionStrategy(final Predicate<Field> strategy)
	{
		checkState(this.inclusionStrategy == null,
				"InclusionStrategy was already set");
		this.inclusionStrategy = checkNotNull(strategy);

		return this;
	}

	/**
	 * Sets a prefix to use when generating the tree
	 * 
	 * @param prefix
	 */
	public SnmpTreeBuilder prefix(final int[] prefix)
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
	 *            The SetStrategy to use
	 */
	public SnmpTreeBuilder setStrategy(final SetStrategy setStrategy)
	{
		checkState(this.setStrategy == null, "SetStrategy was already set");
		this.setStrategy = checkNotNull(setStrategy);

		return this;
	}

	Predicate<Field> getInclusionStrategy()
	{
		return firstNonNull(inclusionStrategy, new DefaultInclusionStrategy());
	}

	int[] getPrefix()
	{
		return firstNonNull(prefix, NO_PREFIX);
	}

	SetStrategy getSetStrategy()
	{
		return firstNonNull(setStrategy, SetStrategy.SETTERS_OR_ANNOTATED);
	}
}
