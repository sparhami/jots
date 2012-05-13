package com.sppad.snmp.exceptions;

public class SnmpBadValueException extends SnmpException
{
    private static final long serialVersionUID = 1L;

    public SnmpBadValueException(String string)
    {
	super(string);
    }
}
