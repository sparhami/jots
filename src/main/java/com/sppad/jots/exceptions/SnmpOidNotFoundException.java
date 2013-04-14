package com.sppad.jots.exceptions;

public class SnmpOidNotFoundException extends SnmpException
{
  private static final long serialVersionUID = 1L;

  public SnmpOidNotFoundException(final String string)
  {
    super(string);
  }
}
