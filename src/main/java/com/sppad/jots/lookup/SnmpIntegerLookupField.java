package com.sppad.jots.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.jots.exceptions.SnmpBadValueException;

public class SnmpIntegerLookupField extends SnmpLookupField
{
  public SnmpIntegerLookupField(
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
    return field.get(enclosingObject);
  }

  @Override
  public void doSet(final String value)
  {
    try
    {
      setValue(Integer.valueOf(value));
    }
    catch (final NumberFormatException e)
    {
      throw new SnmpBadValueException(value);
    }
  }
}
