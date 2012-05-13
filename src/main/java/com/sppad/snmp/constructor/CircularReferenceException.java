package com.sppad.snmp.constructor;

public class CircularReferenceException extends RuntimeException
{

    public CircularReferenceException(String string)
    {
	super(string);
    }

    private static final long serialVersionUID = 1L;

}
