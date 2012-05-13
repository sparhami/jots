package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpPrimativeDoubleLookupField extends SnmpLookupField
{
    public SnmpPrimativeDoubleLookupField(OID oid, Field field, Object object, Method setter)
    {
	super(oid, field, object, setter);
    }

    @Override
    public Object doGet() throws IllegalAccessException
    {
	return field.getDouble(object);
    }

    @Override
    public void doSet(String value)
    {
	try
	{
	    setValue(Double.parseDouble(value));
	}
	catch (NumberFormatException e)
	{
	    throw new SnmpBadValueException(value);
	}
    }
}
