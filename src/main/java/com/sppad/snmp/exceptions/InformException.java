package com.sppad.snmp.exceptions;

public class InformException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public final int index;

    public InformException(String message, Throwable cause, int index) {
	super(message, cause);
	this.index = index;
    }
}
