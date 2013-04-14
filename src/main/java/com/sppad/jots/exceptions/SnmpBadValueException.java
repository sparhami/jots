package com.sppad.jots.exceptions;

public class SnmpBadValueException extends SnmpException
{
  private static final long serialVersionUID = 1L;

  public SnmpBadValueException(final String string)
  {
    super(string);
  }
}
