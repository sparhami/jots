package com.sppad.jots.construction.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.sppad.jots.log.Messages;

/**
 * A basic strategy for determining what fields to include in a generated
 * SnmpTree. Uses annotations to include / exclude fields and whether the field
 * is static/transient/final. The order is as follows:
 * 
 * <ul>
 * <li>Include if annotated with {@link SnmpInclude}
 * <li>Exclude if annotated with {@link SnmpExclude}
 * <li>Exclude if the field is static
 * <li>Exclude if the field is transient
 * <li>Exclude if the field is not final
 * <li>Include
 * <ul>
 */
public class SimpleInclusionStrategy implements Predicate<Field>
{
	/**
	 * Marks that a field should always be skipped when generating an SnmpTree.
	 * This overrides all other considerations except for {@link SnmpInclude}.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface SnmpExclude
	{
	}

	/**
	 * Marks that a field should always be included when generating an SnmpTree.
	 * This overrides all other considerations.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface SnmpInclude
	{
	}

	private static final String INCLUDE_AND_IGNORE_ANNOTATIONS = Messages
			.getString("INCLUDE_AND_IGNORE_ANNOTATIONS");

	private static final Logger logger = LoggerFactory
			.getLogger(SimpleInclusionStrategy.class);

	@Override
	public boolean apply(final Field field)
	{
		final boolean includeAnnotation = field
				.getAnnotation(SnmpInclude.class) != null;
		final boolean excludeAnnotation = field
				.getAnnotation(SnmpExclude.class) != null;
		final int mod = field.getModifiers();

		if (includeAnnotation && excludeAnnotation)
		{
			logger.warn(INCLUDE_AND_IGNORE_ANNOTATIONS,
					field.getDeclaringClass(), field.getName());
		}

		if (includeAnnotation)
			return true;
		else if (excludeAnnotation)
			return false;
		else if (Modifier.isStatic(mod) && !includeStatic())
			return false;
		else if (Modifier.isTransient(mod) && !includeTransient())
			return false;
		else if (!Modifier.isFinal(mod) && !includeNonFinal())
			return false;
		else
			return true;
	}

	protected boolean includeNonFinal()
	{
		return false;
	}

	protected boolean includeStatic()
	{
		return false;
	}

	protected boolean includeTransient()
	{
		return false;
	}
}
