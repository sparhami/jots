package com.sppad.snmp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BooleanInterfaceComment
{
  String type = "Boolean";

  String falseSynopsis();

  String synopsis();

  String trueSynopsis();
}
