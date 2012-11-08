package com.sppad.snmp.constructor.handlers;

import java.lang.reflect.Field;

import com.sppad.snmp.constructor.SnmpTreeConstructor;

public class NullHandler implements ObjectHandler
{
  @Override
  public void handle(SnmpTreeConstructor descender, Object obj, Field field)
  {
  }
}
