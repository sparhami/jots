package com.sppad.snmp.constructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.SortedSet;
import java.util.TreeSet;

import org.snmp4j.smi.OID;

import com.sppad.snmp.lookup.SnmpBooleanLookupField;
import com.sppad.snmp.lookup.SnmpDoubleLookupField;
import com.sppad.snmp.lookup.SnmpEnumLookupField;
import com.sppad.snmp.lookup.SnmpFloatLookupField;
import com.sppad.snmp.lookup.SnmpIntegerLookupField;
import com.sppad.snmp.lookup.SnmpLongLookupField;
import com.sppad.snmp.lookup.SnmpLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeBooleanLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeDoubleLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeFloatLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeIntegerLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeLongLookupField;
import com.sppad.snmp.lookup.SnmpStringLookupField;

public class SnmpTreeSkeleton
{
  private final int[] prefix;

  private final SortedSet<SnmpLookupField> sortSet = new TreeSet<SnmpLookupField>();

  public SnmpTreeSkeleton(int[] prefix)
  {
    this.prefix = prefix;
  }

  public void add(OID oid, Field field, Object object, Method setter)
  {
    SnmpLookupField lookupField = createLookupField(oid, field, object, setter);
    sortSet.add(lookupField);
  }

  private SnmpLookupField createLookupField(OID oid, Field field,
      Object object, Method setter)
  {
    Class<?> fieldType = field.getType();

    // Tried a map / factory pattern here for looking up, but performance
    // was significantly worse. May need to revisit this in the future.
    if (fieldType == Boolean.TYPE)
      return new SnmpPrimativeBooleanLookupField(oid, field, object, setter);

    if (fieldType == Boolean.class)
      return new SnmpBooleanLookupField(oid, field, object, setter);

    if (fieldType == Integer.TYPE)
      return new SnmpPrimativeIntegerLookupField(oid, field, object, setter);

    if (fieldType == Integer.class)
      return new SnmpIntegerLookupField(oid, field, object, setter);

    if (fieldType == Long.TYPE)
      return new SnmpPrimativeLongLookupField(oid, field, object, setter);

    if (fieldType == Long.class)
      return new SnmpLongLookupField(oid, field, object, setter);
    
    if (fieldType == Float.TYPE)
      return new SnmpPrimativeFloatLookupField(oid, field, object, setter);
    
    if (fieldType == Float.class)
      return  new SnmpFloatLookupField(oid, field, object, setter);

    if (fieldType == Double.TYPE)
      return new SnmpPrimativeDoubleLookupField(oid, field, object, setter);
    
    if (fieldType == Double.class)
      return  new SnmpDoubleLookupField(oid, field, object, setter);
    
    if (fieldType == String.class)
      return new SnmpStringLookupField(oid, field, object, setter);
    
    if (fieldType.isEnum())
      return new SnmpEnumLookupField(oid, field, object, setter);
    
    throw new RuntimeException("Class not supported: " + fieldType);
  }

  public SnmpTree finishTreeConstruction()
  {
    return new SnmpTree(prefix, sortSet);
  }
}
