package com.sppad.jots.constructor.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.sppad.jots.constructor.SnmpTreeConstructor;

public interface ObjectHandler
{
  void handle(
      final SnmpTreeConstructor descender,
      final Object obj,
      final Field field)
      throws IllegalArgumentException,
      IllegalAccessException,
      InvocationTargetException;
}
