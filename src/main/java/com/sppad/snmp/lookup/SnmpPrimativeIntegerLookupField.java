package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpPrimativeIntegerLookupField extends SnmpLookupField
{
  public SnmpPrimativeIntegerLookupField(final OID oid, final Field field,
      final Object object, final Method setter)
  {
    super(oid, field, object, setter);
  }

  @Override
  public Object doGet() throws IllegalAccessException
  {
    return field.getInt(object);
  }

  @Override
  public void doSet(final String value)
  {
    try
    {
      setValue(Integer.parseInt(value));
    }
    catch (NumberFormatException e)
    {
      throw new SnmpBadValueException(value);
    }
  }
}
