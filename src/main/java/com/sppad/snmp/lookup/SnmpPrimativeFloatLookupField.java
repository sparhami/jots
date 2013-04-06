package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpPrimativeFloatLookupField extends SnmpLookupField
{
  public SnmpPrimativeFloatLookupField(final OID oid, final Field field,
      final Object object, final Method setter)
  {
    super(oid, field, object, setter);
  }

  @Override
  public Object doGet() throws IllegalAccessException
  {
    return field.getFloat(object);
  }

  @Override
  public void doSet(final String value)
  {
    try
    {
      setValue(Float.parseFloat(value));
    }
    catch (NumberFormatException e)
    {
      throw new SnmpBadValueException(value);
    }
  }
}
