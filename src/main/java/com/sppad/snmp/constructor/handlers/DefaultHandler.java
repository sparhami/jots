package com.sppad.snmp.constructor.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import com.sppad.snmp.annotations.SnmpIgnore;
import com.sppad.snmp.annotations.SnmpInclude;
import com.sppad.snmp.constructor.SnmpTreeConstructor;

public class DefaultHandler implements ObjectHandler
{
  @Override
  public final void handle(SnmpTreeConstructor descender, Object obj,
      Field field) throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException
  {
    descender.descend(obj, obj.getClass(), field);
  }
}
