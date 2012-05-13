package com.sppad.snmp.constructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.SortedSet;
import java.util.TreeSet;

import org.snmp4j.smi.OID;

import com.sppad.snmp.lookup.SnmpBooleanLookupField;
import com.sppad.snmp.lookup.SnmpDoubleLookupField;
import com.sppad.snmp.lookup.SnmpEnumLookupField;
import com.sppad.snmp.lookup.SnmpIntegerLookupField;
import com.sppad.snmp.lookup.SnmpLongLookupField;
import com.sppad.snmp.lookup.SnmpLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeBooleanLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeDoubleLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeIntegerLookupField;
import com.sppad.snmp.lookup.SnmpPrimativeLongLookupField;
import com.sppad.snmp.lookup.SnmpStringLookupField;

public class SnmpTreeSkeleton
{
    private final SortedSet<SnmpLookupField> sortSet = new TreeSet<SnmpLookupField>();

    private final int[] prefix;

    public SnmpTreeSkeleton(int[] prefix)
    {
	this.prefix = prefix;
    }

    public void add(OID oid, Field field, Object object, Method setter)
    {
	SnmpLookupField lookupField = createLookupField(oid, field, object,
		setter);
	sortSet.add(lookupField);
    }

    public SnmpLookupField createLookupField(OID oid, Field field,
	    Object object, Method setter)
    {
	SnmpLookupField lookupField;
	Class<?> fieldType = field.getType();

	// Tried a map / factory pattern here for looking up, but performance
	// was significantly worse. May need to revist this in the future.
	if (fieldType == Boolean.TYPE)
	    lookupField = new SnmpPrimativeBooleanLookupField(oid, field,
		    object, setter);
	else if (fieldType == Boolean.class)
	    lookupField = new SnmpBooleanLookupField(oid, field, object, setter);
	else if (fieldType == Integer.TYPE)
	    lookupField = new SnmpPrimativeIntegerLookupField(oid, field,
		    object, setter);
	else if (fieldType == Integer.class)
	    lookupField = new SnmpIntegerLookupField(oid, field, object, setter);
	else if (fieldType == Long.TYPE)
	    lookupField = new SnmpPrimativeLongLookupField(oid, field, object,
		    setter);
	else if (fieldType == Long.class)
	    lookupField = new SnmpLongLookupField(oid, field, object, setter);
	else if (fieldType == Double.TYPE)
	    lookupField = new SnmpPrimativeDoubleLookupField(oid, field,
		    object, setter);
	else if (fieldType == Double.class)
	    lookupField = new SnmpDoubleLookupField(oid, field, object, setter);
	else if (fieldType == String.class)
	    lookupField = new SnmpStringLookupField(oid, field, object, setter);
	else if (fieldType.isEnum())
	    lookupField = new SnmpEnumLookupField(oid, field, object, setter);
	else
	    throw new RuntimeException("Class not supported: " + fieldType);

	return lookupField;
    }

    public SnmpTree finishTreeConstruction()
    {
	return new SnmpTree(prefix, sortSet);
    }
}
