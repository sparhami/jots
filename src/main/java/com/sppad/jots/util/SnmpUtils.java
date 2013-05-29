package com.sppad.jots.util;

import com.sppad.jots.datastructures.primative.IntStack;

public class SnmpUtils
{
	/**
	 * Finds the common prefix OID for the two given prefixes.
	 * 
	 * @param prefixOne
	 * @param prefixTwo
	 * @return An int array with the common prefix
	 */
	public static int[] commonPrefix(final int[] prefixOne,
			final int[] prefixTwo)
	{
		final int minLength = Math.min(prefixOne.length, prefixTwo.length);
		final IntStack prefix = new IntStack(minLength);

		// add all the OID parts that are the same
		for (int i = 0; i < minLength; i++)
			if (prefixOne[i] != prefixTwo[i])
				break;
			else
				prefix.push(prefixOne[i]);

		return prefix.toArray();
	}

	/**
	 * Transforms the specified String into an int array representing an
	 * explicit SNMP oid table index. This is an array of length n+1 where n is
	 * the length of the source string. The first index is the length of the
	 * string. The remaining indicies contain the integer value of each string
	 * character.
	 * 
	 * @param string
	 *            The string to form the SNMP extension for
	 * @return An int array representing the explicit string index
	 */
	public static int[] getExplicitString(final String string)
	{
		final int length = string.length();

		final int[] oidInts = new int[length + 1];
		oidInts[0] = length;

		for (int i = 0; i < length; i++)
			oidInts[i + 1] = (int) string.charAt(i);

		return oidInts;
	}

	/**
	 * Gets the SNMP table extension for a given object. For Numbers (e.g.
	 * Integer), a single element array containing the number value is returned.
	 * For all other objects, the String representation of the object is used.
	 * 
	 * @param obj
	 *            An object to serve as a table row index
	 * @return An int array representing the table extension
	 */
	public static int[] getSnmpExtension(final Object obj)
	{
		if (obj instanceof Number)
			return new int[] { ((Number) obj).intValue() };
		else
			return getExplicitString(obj.toString());
	}
}
