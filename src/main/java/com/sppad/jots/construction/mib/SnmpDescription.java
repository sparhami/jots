package com.sppad.jots.construction.mib;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.sppad.jots.mib.BooleanInterfaceComment;
import com.sppad.jots.mib.IntegerInterfaceComment;

public class SnmpDescription
{
	private static String getBooleanDescription(final Field field)
	{
		final String descriptionString;

		BooleanInterfaceComment comment = field
				.getAnnotation(BooleanInterfaceComment.class);
		if (comment != null)
		{
			final StringBuilder description = new StringBuilder();
			description.append(comment.synopsis());
			description.append("\n\t\t 'true'  -> ");
			description.append(comment.trueSynopsis());
			description.append("\n\t\t 'false' -> ");
			description.append(comment.falseSynopsis());

			descriptionString = description.toString();
		}
		else
		{
			descriptionString = "No interface documentation";
		}

		return descriptionString;
	}

	public static String getDescription(final Field field)
	{
		final Type type = field.getType();

		if (type.equals(Boolean.class) || type.equals(Boolean.TYPE))
			return getBooleanDescription(field);

		if (type.equals(Integer.class) || type.equals(Integer.TYPE))
			return getIntegerDescription(field);

		return null;
	}

	private static String getIntegerDescription(Field field)
	{
		final String descriptionString;

		IntegerInterfaceComment comment = field
				.getAnnotation(IntegerInterfaceComment.class);
		if (comment != null)
		{
			final StringBuilder description = new StringBuilder();
			description.append(comment.synopsis());
			description.append("\n\t\t 'min value'  -> ");
			description.append(comment.minValue());
			description.append("\n\t\t 'max value' -> ");
			description.append(comment.maxValue());

			descriptionString = description.toString();
		}
		else
		{
			descriptionString = "No interface documentation";
		}

		return descriptionString;
	}
}
