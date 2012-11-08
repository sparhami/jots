package com.sppad.snmp.constructor.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import com.sppad.snmp.constructor.SnmpTreeConstructor;

public class DefaultHandler implements ObjectHandler
{

  @Override
  public final void handle(SnmpTreeConstructor descender, Object obj,
      Field field) throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException
  {
    if (shouldDescend(field))
      descender.descend(obj, obj.getClass(), field);
  }

  protected boolean shouldDescend(Field field)
  {
    int modifiers = field.getModifiers();
    return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
        && Modifier.isFinal(modifiers) && !field.getName().equals("this$0");
  }

}
