package com.sppad.jots.lookup;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.snmp4j.smi.OID;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.sppad.jots.annotations.SnmpNotSettable;
import com.sppad.jots.exceptions.SnmpException;

public class SnmpLookupField implements Comparable<SnmpLookupField>
{
  private static final Map<Class<?>, Function<String, Object>> converterLookupMap = ImmutableMap
      .<Class<?>, Function<String, Object>> builder()
      .put(Boolean.TYPE, ValueConverters.CONVERT_TO_BOOLEAN)
      .put(Boolean.class, ValueConverters.CONVERT_TO_BOOLEAN)
      .put(Integer.TYPE, ValueConverters.CONVERT_TO_INT)
      .put(Integer.class, ValueConverters.CONVERT_TO_INT)
      .put(Long.TYPE, ValueConverters.CONVERT_TO_LONG)
      .put(Long.class, ValueConverters.CONVERT_TO_LONG)
      .put(Float.TYPE, ValueConverters.CONVERT_TO_FLOAT)
      .put(Float.class, ValueConverters.CONVERT_TO_FLOAT)
      .put(Double.TYPE, ValueConverters.CONVERT_TO_DOUBLE)
      .put(Double.class, ValueConverters.CONVERT_TO_DOUBLE)
      .put(String.class, ValueConverters.CONVERT_TO_STRING).build();

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static SnmpLookupField create(
      final OID oid,
      final Field field,
      final Object object,
      final Method setter)
  {
    final Class<?> fieldType = field.getType();

    final Function<String, Object> valueConverter = fieldType.isEnum() //
    ? ValueConverters.enumConverter((Class<? extends Enum>) fieldType) //
        : converterLookupMap.get(fieldType);

    if (valueConverter == null)
    {
      throw new RuntimeException("Class not supported: " + fieldType);
    }

    return new SnmpLookupField(oid, field, object, setter, valueConverter);
  }

  /** The object that corresponds to this OID instance */
  final Object enclosingObject;

  /** The field object for this OID */
  final Field field;

  /** The OID object for this field */
  final OID oid;

  /** The method that should be used when performing a set */
  final Method setter;

  /** Whether the field is writable or not */
  final boolean writable;

  /** Used for converting a String value into an Object for setting on the field */
  final Function<String, Object> valueConverter;

  /**
   * Constructs an snmpLookupField object.
   * 
   * @param oid
   * @param field
   * @param object
   */
  SnmpLookupField(
      final OID oid,
      final Field field,
      final Object enclosingObject,
      final Method setter,
      final Function<String, Object> valueConverter)
  {
    this.oid = oid;
    this.field = field;
    this.enclosingObject = enclosingObject;
    this.setter = setter;
    this.writable = checkIsWritable();
    this.valueConverter = valueConverter;
  }

  /**
   * Checks to see if the field is 'settable', meaning it either as a set method
   * and the field does not have an annotation to prevent it from being set.
   * 
   * @return True if this field is 'settable', false otherwise.
   * @see SnmpNotSettable
   */
  private boolean checkIsWritable()
  {
    if (field.getAnnotation(SnmpNotSettable.class) != null || setter == null)
      return false;
    else
      return true;
  }

  /**
   * Compares the OID of this field to the given field.
   */
  @Override
  public int compareTo(final SnmpLookupField o)
  {
    return oid.compareTo(o.oid);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Annotation getAnnotation(final Class annotationClass)
  {
    return field.getAnnotation(annotationClass);
  }

  public Object getEnclosingObject()
  {
    return enclosingObject;
  }

  public String getFieldName()
  {
    return field.getName();
  }

  /**
   * @return The OID object corresponding to this field
   */
  public OID getOid()
  {
    return oid;
  }

  /**
   * @return The type of this field.
   */
  public Type getType()
  {
    return field.getType();
  }

  /**
   * Performs a get, implementation specific to the type of a field.
   * 
   * @return An object representing the value of this field when the method is
   *         called.
   */
  public Object getValue()
  {
    try
    {
      return field.get(enclosingObject);
    }
    catch (final IllegalAccessException e)
    {
      throw new SnmpException(e);
    }
  }

  /**
   * @return Whether or not this field is considered to be writable
   */
  public boolean isWritable()
  {
    return writable;
  }

  /**
   * Sets the value of the field by using a setter if available, or directly
   * otherwise. This does not check if the field is considered 'settable'.
   * {@link #isWritable()} can be used to check if the field is considered
   * 'settable'.
   * 
   * @param value
   *          The value to set.
   */
  public void set(String value)
  {
    setValue(valueConverter.apply(value));
  }

  void setValue(final Object value)
  {
    try
    {
      if (setter != null)
        setter.invoke(enclosingObject, value);
      else
        field.set(enclosingObject, value);
    }
    catch (
        SecurityException |
        IllegalAccessException |
        InvocationTargetException e)
    {
      throw new SnmpException(e.getCause().getMessage());
    }
  }
}
