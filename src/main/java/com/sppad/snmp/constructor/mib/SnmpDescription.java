package com.sppad.snmp.constructor.mib;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.sppad.snmp.annotations.BooleanInterfaceComment;
import com.sppad.snmp.annotations.IntegerInterfaceComment;

public class SnmpDescription
{
  public static String getDescription(Field field)
  {
    Type type = field.getType();

    if (type.equals(Boolean.class) || type.equals(Boolean.TYPE))
      return getBooleanDescription(field);

    if (type.equals(Integer.class) || type.equals(Integer.TYPE))
      return getIntegerDescription(field);

    return null;
  }

  private static String getBooleanDescription(Field field)
  {
    String descriptionString;

    BooleanInterfaceComment comment = field
        .getAnnotation(BooleanInterfaceComment.class);
    if (comment != null)
    {
      StringBuilder description = new StringBuilder();
      description.append(comment.synopsis());
      description.append("\n\t\t 'true'  -> ");
      description.append(comment.trueSynopsis());
      description.append("\n\t\t 'false' -> ");
      description.append(comment.falseSynopsis());

      descriptionString = description.toString();
    }
    else
    {
      descriptionString = "No interface documentation";
    }

    return descriptionString;
  }

  private static String getIntegerDescription(Field field)
  {
    String descriptionString;

    IntegerInterfaceComment comment = field
        .getAnnotation(IntegerInterfaceComment.class);
    if (comment != null)
    {
      StringBuilder description = new StringBuilder();
      description.append(comment.synopsis());
      description.append("\n\t\t 'min value'  -> ");
      description.append(comment.minValue());
      description.append("\n\t\t 'max value' -> ");
      description.append(comment.maxValue());

      descriptionString = description.toString();
    }
    else
    {
      descriptionString = "No interface documentation";
    }

    return descriptionString;
  }
}
