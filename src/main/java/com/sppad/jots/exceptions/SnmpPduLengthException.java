package com.sppad.jots.exceptions;

public class SnmpPduLengthException extends SnmpException
{
  private static final long serialVersionUID = 1L;

  public SnmpPduLengthException(final String string)
  {
    super(string);
  }
}