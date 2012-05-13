package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpPrimativeBooleanLookupField extends SnmpLookupField
{
    public SnmpPrimativeBooleanLookupField(OID oid, Field field, Object object, Method setter)
    {
	super(oid, field, object, setter);
    }

    @Override
    public Object doGet() throws IllegalAccessException
    {
	return field.getBoolean(object);
    }

    @Override
    public void doSet(String value)
    {
	if ("true".equalsIgnoreCase(value))
	    setValue(true);
	else if ("false".equalsIgnoreCase(value))
	    setValue(false);
	else
	    throw new SnmpBadValueException(value);
    }
}
