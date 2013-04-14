package com.sppad.jots.util;

import java.util.HashSet;
import java.util.Set;

import com.sppad.jots.datastructures.primative.IntStack;

public class SnmpUtils
{
  private static final Set<Class<?>> builtinClasses = new HashSet<Class<?>>();

  static
  {
    builtinClasses.add(Boolean.class);
    builtinClasses.add(Byte.class);
    builtinClasses.add(Integer.class);
    builtinClasses.add(Long.class);
    builtinClasses.add(Float.class);
    builtinClasses.add(Double.class);
    builtinClasses.add(Character.class);
    builtinClasses.add(String.class);
  }

  /**
   * Finds the common prefix OID for the two given prefixes.
   * 
   * @param prefixOne
   * @param prefixTwo
   * @return An int array with the common prefix
   */
  public static int[] findCommonPrefix(
      final int[] prefixOne,
      final int[] prefixTwo)
  {
    final int minPrefixLength = Math.min(prefixOne.length, prefixTwo.length);
    final IntStack prefix = new IntStack(minPrefixLength);

    // add all the OID parts that are the same
    for (int i = 0; i < minPrefixLength; i++)
      if (prefixOne[i] != prefixTwo[i])
        break;
      else
        prefix.push(prefixOne[i]);

    return prefix.toArray();
  }

  /**
   * Transforms the specified String into an int array representing an explicit
   * SNMP oid table index. This is an array of length n+1 where n is the length
   * of the source string. The first index is the length of the string. The
   * remaining indicies contain the integer value of each string character.
   * 
   * @param string
   *          The string to form the SNMP extension for
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
   * Constructs the setter name for the given fieldName. This is done by adding
   * the prefix "set" and capitalizing the first letter of the given field name.
   * <p>
   * For example, a field named <b>someField</b> would have the corresponding
   * setter <b>setSomeField</b>
   * 
   * @param fieldName
   *          The name of the field to get the setter name for
   * @return A string representing the setter name.
   */
  public static String getSetterName(final String fieldName)
  {
    final char firstLetter = fieldName.charAt(0);

    final StringBuilder setMethodName = new StringBuilder("set");
    setMethodName.append(fieldName);
    setMethodName.setCharAt(3, Character.toUpperCase(firstLetter));

    return setMethodName.toString();
  }

  /**
   * Gets the SNMP table extension for a given object. For Numbers (e.g.
   * Integer), a single element array containing the number value is returned.
   * For all other objects, the String representation of the object is used.
   * 
   * @param obj
   *          An object to serve as a table row index
   * @return An int array representing the table extension
   */
  public static int[] getSnmpExtension(final Object obj)
  {
    if (obj instanceof Number)
      return new int[] { ((Number) obj).intValue() };
    else
      return getExplicitString(obj.toString());
  }

  /**
   * Checks whether a given class is a primitive wrapper (e.g. Integer.class) or
   * is a String.
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
   * @return True if the class is a primitive/primitive wrapper, a String or an
   *         enum, false otherwise
   */
  public static boolean isSimple(final Class<?> klass)
  {
    return isBuiltin(klass) || isPrimitive(klass) || klass.isEnum();
  }
}
