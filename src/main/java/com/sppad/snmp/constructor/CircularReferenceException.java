package com.sppad.snmp.constructor;

public class CircularReferenceException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    public CircularReferenceException(String string)
    {
	super(string);
    }

}
