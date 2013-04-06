package com.sppad.snmp.lookup;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.snmp4j.smi.OID;

import com.sppad.snmp.annotations.SnmpNotSettable;
import com.sppad.snmp.exceptions.SnmpException;

public abstract class SnmpLookupField implements Comparable<SnmpLookupField>
{
  /** The field object for this OID */
  final Field field;

  /** The object that corresponds to this OID instance */
  final Object object;

  /** The OID object for this field */
  final OID oid;

  final Method setter;

  final boolean writable;

  /**
   * Constructs an snmpLookupField object.
   * 
   * @param oid
   * @param field
   * @param object
   */
  protected SnmpLookupField(
      final OID oid,
      final Field field,
      final Object object,
      final Method setter)
  {
    this.oid = oid;
    this.field = field;
    this.object = object;
    this.setter = setter;
    this.writable = checkIsWritable();
  }

  /**
   * Compares the OID of this field to the given field.
   */
  @Override
  public int compareTo(final SnmpLookupField o)
  {
    return oid.compareTo(o.oid);
  }

  /**
   * Performs a get, implementation specific to the type of a field.
   * 
   * @return An object representing the value of this field when the method is
   *         called.
   */
  public Object get()
  {
    try
    {
      return doGet();
    }
    catch (final IllegalAccessException e)
    {
      throw new SnmpException(e);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Annotation getAnnotation(final Class annotationClass)
  {
    return field.getAnnotation(annotationClass);
  }

  public String getFieldName()
  {
    return field.getName();
  }

  public Object getObject()
  {
    return object;
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
    doSet(value);
  }

  /**
   * Checks to see if the field is 'settable', meaning it either as a set method
   * and the field does not have an annotation to prevent it from being set.
   * 
   * @return True if this field is 'settable', false otherwise.
   * @see SnmpNotSettable
   */
  protected boolean checkIsWritable()
  {
    if (field.getAnnotation(SnmpNotSettable.class) != null || setter == null)
      return false;
    else
      return true;
  }

  protected void setValue(final Object value)
  {
    try
    {
      if (setter != null)
        setter.invoke(object, value);
      else
        field.set(object, value);
    }
    catch (SecurityException e)
    {
      throw new SnmpException(e.getCause().getMessage());
    }
    catch (IllegalAccessException e)
    {
      throw new SnmpException(e.getCause().getMessage());
    }
    catch (InvocationTargetException e)
    {
      throw new SnmpException(e.getCause());
    }
  }

  /**
   * Performs a get, implementation specific to the type of a field.
   * 
   * @return An object representing the value of this field when the method is
   *         called.
   * @throws IllegalAccessException
   */
  abstract Object doGet()
      throws IllegalAccessException;

  /**
   * Performs a set, implementation specific to the type of a field. All
   * validation of data should be done by the implementing class.
   * 
   * @param value
   *          The value to set.
   */
  abstract void doSet(final String value);
}
