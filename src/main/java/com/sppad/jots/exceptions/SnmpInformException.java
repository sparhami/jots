package com.sppad.jots.exceptions;

public class SnmpInformException extends Exception
{
	private static final long serialVersionUID = 1L;

	public final int index;

	public SnmpInformException(final String message, final Throwable cause,
			final int index)
	{
		super(message, cause);
		this.index = index;
	}
}
