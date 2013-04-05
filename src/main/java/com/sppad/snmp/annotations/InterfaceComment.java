package com.sppad.snmp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to generate the description field for an entry in a generated MIB.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
  value = { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface InterfaceComment
{
  String synopsis();
}
