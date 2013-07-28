package com.sppad.jots.construction.nodes;

import java.lang.reflect.Field;

public class EntryNode extends InnerNode
{
	public EntryNode(final Field field, final Node parent)
	{
		super(field.getType(), parent, parent.inTable, field.getName(), field);
	}

	@Override
	void accept(final INodeVisitor visitor)
	{
		visitor.visitEnter(this);

		for (final Node child : snmpNodes)
			child.accept(visitor);

		visitor.visitExit(this);
	}
}
