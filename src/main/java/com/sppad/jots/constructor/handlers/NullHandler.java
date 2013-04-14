package com.sppad.jots.constructor.handlers;

import java.lang.reflect.Field;

import com.sppad.jots.constructor.SnmpTreeConstructor;

public class NullHandler implements ObjectHandler
{
  @Override
  public void handle(
      final SnmpTreeConstructor descender,
      final Object obj,
      final Field field)
  {
  }
}
