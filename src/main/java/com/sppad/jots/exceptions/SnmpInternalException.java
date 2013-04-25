package com.sppad.jots.exceptions;

public class SnmpInternalException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public SnmpInternalException(Exception cause)
	{
		super(cause);
	}

	public SnmpInternalException(String string)
	{
		super(string);
	}

}
