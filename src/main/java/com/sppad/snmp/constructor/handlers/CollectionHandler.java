package com.sppad.snmp.constructor.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import com.sppad.snmp.constructor.ClassInfo;
import com.sppad.snmp.constructor.SnmpTreeConstructor;

public class CollectionHandler implements ObjectHandler
{

  /**
   * Get the value type: if the type can be found, use that otherwise, find the
   * common ancestor class of all the values in the collection, and treat that
   * as the collection type. This needs to be done since there are no reified
   * generics yet and we need to be able find something to act as the type for
   * this collection.
   * 
   * @param field
   * @param collection
   * @return
   */
  @SuppressWarnings("unchecked")
  public Class<?> getValueType(final Field field, final Collection<?> collection)
  {
    final ParameterizedType pType = (ParameterizedType) field.getGenericType();
    if (pType.getActualTypeArguments()[0] instanceof Class<?>)
      return (Class<?>) pType.getActualTypeArguments()[0];
    else
      return ClassInfo
          .getLeastCommonSuperclassForObjects((Collection<Object>) collection);
  }

  @Override
  public final void handle(final SnmpTreeConstructor descender,
      final Object obj, final Field field) throws IllegalArgumentException,
      IllegalAccessException, InvocationTargetException
  {
    final Collection<?> collection = (Collection<?>) obj;

    descender.onCollectionEnter(obj, field, Object.class);

    final Class<?> valueType = getValueType(field, collection);

    // note that index starts at 1, since .0 by convention indicates a
    // terminal OID and not a table entry
    int index = 1;
    for (final Object item : collection)
    {
      if (item == null)
        continue;

      descender.onNextCollectionValue(item, index++);
      descender.descend(item, valueType, field);
    }

    descender.onCollectionExit(obj, field);
  }
}
