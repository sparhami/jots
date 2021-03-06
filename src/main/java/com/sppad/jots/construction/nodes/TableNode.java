package com.sppad.jots.construction.nodes;

import java.lang.reflect.Field;

import com.sppad.jots.annotations.Jots;

public class TableNode extends InnerNode
{
	public TableNode(final Field field, final Node parent)
	{
		super(field.getAnnotation(Jots.class).cls(), parent, true, field
				.getName(), field);
	}

	@Override
	public String getEnding()
	{
		return "Table";
	}

	public TableEntryNode getEntry()
	{
		return (TableEntryNode) snmpNodes.iterator().next();
	}

	@Override
	public Node getSnmpParentNode(Node parent)
	{
		do
		{
			// Note: the root node will always have inTable = false
			if (!parent.inTable || (parent instanceof TableEntryNode))
			{
				break;
			}
		}
		while ((parent = parent.parent) != null);

		return parent;
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
