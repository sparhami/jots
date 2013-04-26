package com.sppad.jots.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the type of the values in a Collection, due to a lack of
 * reified generics making it impossible to determine at runtime the type of the
 * type parameter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Jots
{
	Class<?> cls();
}
