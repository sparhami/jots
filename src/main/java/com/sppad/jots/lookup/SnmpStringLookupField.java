package com.sppad.jots.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

public class SnmpStringLookupField extends SnmpLookupField
{
  public SnmpStringLookupField(
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
    setValue(value);
  }
}
