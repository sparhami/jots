package com.sppad.snmp.constructor;

public class CircularReferenceException extends RuntimeException
{

  private static final long serialVersionUID = 1L;

  public CircularReferenceException(final String string)
  {
    super(string);
  }

}
