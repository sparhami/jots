package com.sppad.snmp.constructor.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

import com.sppad.snmp.constructor.ClassInfo;
import com.sppad.snmp.constructor.SnmpTreeConstructor;

public class MapHandler implements ObjectHandler
{
  /**
   * Get the value type: if the type can be found, use that otherwise, find the
   * common ancestor class of all the values in the collection, and treat that
   * as the collection type. This needs to be done since there are no reified
   * generics yet and we need to be able find something to act as the type for
   * this collection.
   * <p>
   * For example, if Map<String, Foo> is used. Foo.class is returned. However,
   * if Map<String, R> is used, then there is no general way to tell what R is
   * at runtime. While it is possible to find what R is if R is a parameter to
   * the current class, it is not possible if R itself is generic.
   * <p>
   * That is, if we have a class, Foo
   * <Q, R>that has a Map<String, R>, and we have a class Bar extends
   * Foo<Object, Integer>, we can tell that the Map is Map<String, Integer>. But
   * if we have Bar <A, B> extends Foo<A, B> and FooBar extends Bar<String,
   * Integer>, there is no way to find this information through generics, even
   * though the JVM may have this information, depending on the implementation.
   * 
   * @param field
   * @param collection
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Class<?> getValueType(final Field field, final Map<?, ?> map)
  {

    final ParameterizedType pType = (ParameterizedType) field.getGenericType();
    if (pType.getActualTypeArguments()[1] instanceof Class<?>)
      return (Class<?>) pType.getActualTypeArguments()[1];
    else
      return ClassInfo
          .getLeastCommonSuperclassForObjects((Collection<Object>) map.values());
  }

  @Override
  public final void handle(
      final SnmpTreeConstructor descender,
      final Object obj,
      final Field field)
      throws IllegalArgumentException,
      IllegalAccessException,
      InvocationTargetException
  {
    final Map<?, ?> map = (Map<?, ?>) obj;
    final ParameterizedType pType = (ParameterizedType) field.getGenericType();

    // find the key type for the map, if it can be found
    Class<?> keyType = Object.class;
    if (pType.getActualTypeArguments()[0] instanceof Class<?>)
      keyType = (Class<?>) pType.getActualTypeArguments()[0];

    final Class<?> valueType = getValueType(field, map);

    descender.onCollectionEnter(obj, field, keyType);

    for (final Map.Entry<?, ?> entry : map.entrySet())
    {
      if (entry.getValue() == null)
        continue;

      descender.onNextCollectionValue(entry.getValue(), entry.getKey());
      descender.descend(entry.getValue(), valueType, field);
    }

    descender.onCollectionExit(obj, field);
  }
}
