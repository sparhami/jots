package com.sppad.snmp.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks the field as not being settable from SNMP.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SnmpNotSettable
{

}
