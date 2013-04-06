package com.sppad.snmp.constructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import com.sppad.snmp.annotations.SnmpTableIndex;

public class ClassInfo
{
  /** The field that should be used for a table extension, if applicable */
  public final Field extensionField;

  /** All the fields in the class and its super classes, except for Object */
  public final Collection<Field> fields;

  /**
   * Finds the fields in the current object that should be descended into for
   * performing the walk. These include all fields that are not static and not
   * marked as transient.
   * <p>
   * Fields from the super classes of the specified object up to and including
   * the specified base class are included. Object.class can be specified if all
   * fields are desired.
   * 
   * @param baseClass
   *          The base Class to use for the object.
   * @return The list of fields that should be descended into.
   */
  public static Collection<Field> getFields(final Class<?> baseClass)
  {
    final Collection<Field> fields = new ArrayList<Field>();
    Class<?> currentClass = baseClass;

    // reverse the order so superclass fields come first
    final Deque<Class<?>> classStack = new LinkedList<Class<?>>();
    while (currentClass != Object.class)
    {
      classStack.push(currentClass);
      currentClass = currentClass.getSuperclass();
    }

    for (final Class<?> klass : classStack)
      for (final Field field : klass.getDeclaredFields())
        fields.add(field);

    return fields;
  }

  /**
   * Returns the least common superclass for two classes. Note that this does
   * not search interfaces, so if a interface is the only common ancestor,
   * Object.class will be returned.
   * 
   * @param klassOne
   * @param klassTwo
   * @return The least common superclass.
   */
  public static Class<?> getLeastCommonSuperclass(
      final Class<?> klassOne,
      final Class<?> klassTwo)
  {
    if (klassOne.isAssignableFrom(klassTwo))
      return klassOne;
    if (klassTwo.isAssignableFrom(klassOne))
      return klassTwo;

    return getLeastCommonSuperclass(klassOne.getSuperclass(),
        klassTwo.getSuperclass());
  }

  /**
   * Returns the least common superclass for a list of classes. Note that this
   * does not search interfaces, so if a interface is the only common ancestor,
   * Object.class will be returned.
   * 
   * @param classList
   * @return The least common superclass.
   */
  public static Class<?> getLeastCommonSuperclass(
      final Collection<Class<?>> classList)
  {
    if (classList.size() == 0)
      return Object.class;

    Class<?> parentClass = classList.iterator().next();
    for (final Class<?> klass : classList)
      if (klass != parentClass)
        parentClass = getLeastCommonSuperclass(klass, parentClass);

    return parentClass;
  }

  /**
   * Returns the least common superclass for a list of object. Note that this
   * does not search interfaces, so if a interface is the only common ancestor,
   * Object.class will be returned.
   * 
   * @param objectList
   * @return The least common superclass.
   */
  public static Class<?> getLeastCommonSuperclassForObjects(
      final Collection<Object> objectList)
  {
    if (objectList.size() == 0)
      return Object.class;

    Class<?> parentClass = objectList.iterator().next().getClass();
    for (final Object obj : objectList)
      if (obj.getClass() != parentClass)
        parentClass = getLeastCommonSuperclass(obj.getClass(), parentClass);

    return parentClass;
  }

  public ClassInfo(final Class<?> klass)
  {
    Field extensionField = null;
    for (Field field : klass.getDeclaredFields())
      if (field.isAnnotationPresent(SnmpTableIndex.class))
        extensionField = field;

    this.extensionField = extensionField;
    this.fields = getFields(klass);
  }
}
