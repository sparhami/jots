package com.sppad.jots.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks the field as not being able to be set from SNMP.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SnmpNotSettable
{

}
