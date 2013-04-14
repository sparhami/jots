package com.sppad.jots.exceptions;

public class SnmpNoMoreEntriesException extends SnmpException
{
  private static final long serialVersionUID = 1L;

  public SnmpNoMoreEntriesException(final String string)
  {
    super(string);
  }
}
