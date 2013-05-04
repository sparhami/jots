package com.sppad.jots.construction;

import java.lang.reflect.Field;

abstract class InnerNode extends Node
{
	InnerNode(final Class<?> klass, final Node parent, final boolean inTable,
			final String name, final Field field)
	{
		super(klass, parent, inTable, name, field);
	}
}
