package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpDoubleLookupField extends SnmpLookupField
{
  public SnmpDoubleLookupField(OID oid, Field field, Object object,
      Method setter)
  {
    super(oid, field, object, setter);
  }

  @Override
  public Object doGet() throws IllegalAccessException
  {
    return field.get(object);
  }

  @Override
  public void doSet(String value)
  {
    try
    {
      setValue(Double.valueOf(value));
    }
    catch (NumberFormatException e)
    {
      throw new SnmpBadValueException(value);
    }
  }
}
