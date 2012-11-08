package com.sppad.snmp.constructor.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.sppad.snmp.constructor.SnmpTreeConstructor;

public interface ObjectHandler
{
  void handle(SnmpTreeConstructor descender, Object obj, Field field)
      throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException;
}
