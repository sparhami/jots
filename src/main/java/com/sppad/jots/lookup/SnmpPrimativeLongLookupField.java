package com.sppad.jots.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.jots.exceptions.SnmpBadValueException;

public class SnmpPrimativeLongLookupField extends SnmpLookupField
{
  public SnmpPrimativeLongLookupField(
      final OID oid,
      final Field field,
      final Object object,
      final Method setter)
  {
    super(oid, field, object, setter);
  }

  @Override
  public Object doGet()
      throws IllegalAccessException
  {
    return field.getLong(enclosingObject);
  }

  @Override
  public void doSet(final String value)
  {
    try
    {
      setValue(Long.parseLong(value));
    }
    catch (NumberFormatException e)
    {
      throw new SnmpBadValueException(value);
    }
  }
}