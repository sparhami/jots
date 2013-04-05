package com.sppad.snmp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that a field should always be skipped when generating an SnmpTree. This
 * overrides all other considerations except for {@link SnmpInclude}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SnmpIgnore
{

}
