package com.sppad.jots.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to generate the description field for a Boolean entry in a generated
 * MIB.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BooleanInterfaceComment
{
	final String type = "Boolean";

	String falseSynopsis();

	String synopsis();

	String trueSynopsis();
}
