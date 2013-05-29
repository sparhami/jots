package com.sppad.jots.construction.nodes;

import java.lang.reflect.Field;

public class LeafNode extends Node
{
	public LeafNode(final Field field, final Node parent)
	{
		super(field.getType(), parent, parent.inTable, field.getName(), field);
	}

	void accept(final INodeVisitor visitor)
	{
		visitor.visitEnter(this);
		visitor.visitExit(this);
	}

}
