package com.sppad.jots.exceptions;

public class SnmpNotWritableException extends Exception
{
	private static final long serialVersionUID = 1L;

	public SnmpNotWritableException(final String string)
	{
		super(string);
	}
}
