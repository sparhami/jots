package com.sppad.jots.util;

public class Strings
{
	public static String firstCharToUppercase(final String string)
	{
		if (string.length() == 0)
			return string;

		final StringBuilder builder = new StringBuilder(string);
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

		return builder.toString();
	}

	public static String firstCharToLowercase(final String string)
	{
		if (string.length() == 0)
			return string;

		final StringBuilder builder = new StringBuilder(string);
		builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));

		return builder.toString();
	}
}
