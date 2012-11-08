package com.sppad.snmp.exceptions;

public class SnmpException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public SnmpException(String string)
  {
    super(string);
  }

  public SnmpException(Throwable t)
  {
    super(t);
  }

}
