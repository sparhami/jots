package com.sppad.jots.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class FieldUtils
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
   * @param field
   *          The Field to get the corresponding setter for
   * @return Null if the setter does not exist or cannot be accessed, the setter
   *         Method otherwise.
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
