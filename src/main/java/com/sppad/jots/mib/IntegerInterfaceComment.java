package com.sppad.jots.mib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to generate the description field for an Integer entry in a generated
 * MIB.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IntegerInterfaceComment
{
	final String type = "Integer";

	int maxValue();

	int minValue();

	String synopsis();
}
