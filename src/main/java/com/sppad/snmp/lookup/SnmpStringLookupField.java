package com.sppad.snmp.lookup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.snmp4j.smi.OID;

public class SnmpStringLookupField extends SnmpLookupField
{
    public SnmpStringLookupField(OID oid, Field field, Object object, Method setter)
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
	setValue(value);
    }
}
