package com.sppad.snmp.exceptions;

public class SnmpException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public SnmpException(final String string)
  {
    super(string);
  }

  public SnmpException(final Throwable t)
  {
    super(t);
  }

}
