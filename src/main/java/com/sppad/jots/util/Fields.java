package com.sppad.jots.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

public class Fields
{
	private static final ImmutableSet<Class<?>> builtinClasses = new ImmutableSet.Builder<Class<?>>()
			.add(Boolean.class, Byte.class, Integer.class, Long.class,
					Float.class, Double.class, Character.class, String.class)
			.build();

	private static final Predicate<Field> REMOVE_SYNTHETIC = new Predicate<Field>() {
		@Override
		public boolean apply(final Field field)
		{
			return !field.isSynthetic();
		}
	};

	/**
	 * @param cls
	 *            The class to get the Fields for
	 * @return A Collection of Fields contained in the class and its non-Object
	 *         super-classes, with those of a super class appearing before the
	 *         extending class. Does not include synthetic fields.
	 * 
	 * @see Field#isSynthetic()
	 */
	public static Collection<Field> getFields(final Class<?> cls)
	{
		final List<Field> fields = new ArrayList<Field>();

		for (Class<?> c = cls; c != Object.class; c = c.getSuperclass())
			fields.addAll(0, Arrays.asList(c.getDeclaredFields()));

		return Collections2.filter(fields, REMOVE_SYNTHETIC);
	}

	/**
	 * @param field
	 *            The Field to get the corresponding setter for
	 * @return Null if the setter does not exist or cannot be accessed, the
	 *         setter Method otherwise.
	 */
	public static Method getSetterForField(final Field field)
	{
		try
		{
			final Class<?> cls = field.getDeclaringClass();
			final String name = getSetterName(field.getName());

			return cls.getMethod(name, field.getType());
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			return null;
		}
	}

	/**
	 * Constructs the setter name for the given fieldName. This is done by
	 * adding the prefix "set" and capitalizing the first letter of the given
	 * field name.
	 * <p>
	 * For example, a field named <b>someField</b> would have the corresponding
	 * setter <b>setSomeField</b>
	 * 
	 * @param fieldName
	 *            The name of the field to get the setter name for
	 * @return A string representing the setter name.
	 */
	public static String getSetterName(final String fieldName)
	{
		final StringBuilder methodName = new StringBuilder(
				fieldName.length() + 3);
		methodName.append("set");
		methodName.append(fieldName);
		methodName.setCharAt(3, Character.toUpperCase(fieldName.charAt(0)));

		return methodName.toString();
	}

	/**
	 * Checks whether a given class is a primitive wrapper (e.g. Integer.class)
	 * or is a String.
	 * 
	 * @return True if the class is a built-in class, false otherwise
	 */
	public static boolean isBuiltin(final Class<?> klass)
	{
		return builtinClasses.contains(klass);
	}

	/**
	 * @return True if the class is a primitive (e.g. int), false otherwise
	 */
	public static boolean isPrimitive(final Class<?> klass)
	{
		return klass.isPrimitive();
	}

	/**
	 * @return True if the class is a primitive/primitive wrapper, a String or
	 *         an enum, false otherwise
	 */
	public static boolean isSimple(final Class<?> klass)
	{
		return isBuiltin(klass) || isPrimitive(klass) || klass.isEnum();
	}
}
