package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpBooleanLookupField extends SnmpLookupField
{
  private static final String FALSE_STRING = "false";

  private static final String TRUE_STRING = "true";

  public SnmpBooleanLookupField(
      final OID oid,
      final Field field,
      final Object object,
      Method setter)
  {
    super(oid, field, object, setter);
  }

  @Override
  public Object doGet()
      throws IllegalAccessException
  {
    return field.get(object);
  }

  @Override
  public void doSet(final String value)
  {
    if (TRUE_STRING.equalsIgnoreCase(value))
      setValue(true);
    else if (FALSE_STRING.equalsIgnoreCase(value))
      setValue(false);
    else
      throw new SnmpBadValueException(value);
  }
}
