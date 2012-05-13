package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpIntegerLookupField extends SnmpLookupField
{
    public SnmpIntegerLookupField(OID oid, Field field, Object object, Method setter)
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
	    setValue(Integer.valueOf(value));
	}
	catch (NumberFormatException e)
	{
	    throw new SnmpBadValueException(value);
	}
    }
}
