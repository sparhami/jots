package com.sppad.snmp.util;

import java.util.HashSet;
import java.util.Set;

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

  public static int[] combineArrays(int[]... arguments)
  {
    int size = 0;

    for (int[] part : arguments)
      size += part.length;

    int[] combined = new int[size];
    int lastIndex = 0;
    for (int[] part : arguments)
    {
      System.arraycopy(part, 0, combined, lastIndex, part.length);
      lastIndex = part.length;
    }

    return combined;
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
  public static int[] getExplicitString(String string)
  {
    int length = string.length();

    int[] oidInts = new int[length + 1];
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
  public static String getSetterName(String fieldName)
  {
    char firstLetter = fieldName.charAt(0);

    StringBuilder setMethodName = new StringBuilder("set");
    setMethodName.append(fieldName);
    setMethodName.setCharAt(3, Character.toUpperCase(firstLetter));

    return setMethodName.toString();
  }

  public static int[] getSnmpExtension(Object obj)
  {
    if (obj instanceof Number)
      return new int[] { ((Number) obj).intValue() };
    else
      return getExplicitString(obj.toString());
  }

  public static boolean isBuiltin(Class<?> klass)
  {
    return builtinClasses.contains(klass);
  }

  public static boolean isPrimtive(Class<?> klass)
  {
    return klass.isPrimitive();
  }

  public static boolean isSimple(Class<?> klass)
  {
    return isBuiltin(klass) || isPrimtive(klass) || klass.isEnum();
  }
}
