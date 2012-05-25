package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpEnumLookupField extends SnmpLookupField
{
    @SuppressWarnings("rawtypes")
    private final Class<? extends Enum> enumClass;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public SnmpEnumLookupField(OID oid, Field field, Object object, Method setter)
    {
	super(oid, field, object, setter);

	this.enumClass = (Class<? extends Enum>) field.getType();
    }

    @Override
    public Object doGet() throws IllegalAccessException
    {
	return field.get(object).toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doSet(String value)
    {
	try
	{
	    this.setValue(Enum.valueOf(enumClass, value));
	}
	catch (IllegalArgumentException e)
	{
	    throw new SnmpBadValueException(String.format("Value %s is not valid for this field", value));
	}
    }

}
