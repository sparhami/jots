package com.sppad.jots.construction;

import java.lang.reflect.Field;

import com.sppad.jots.annotations.Jots;
import com.sppad.jots.util.SnmpUtils;

class TableNode extends InnerNode
{
	private Field indexField;

	TableNode(final Field field, final Node parent)
	{
		super(field.getAnnotation(Jots.class).cls(), parent, true, field
				.getName(), field);
	}

	void accept(final INodeVisitor visitor)
	{
		visitor.visitEnter(this);

		for (final Node child : snmpNodes)
			child.accept(visitor);

		visitor.visitExit(this);
	}

	@Override
	Node getSnmpParentNode(Node parent)
	{
		do
		{
			// Note: the root node will always have inTable = false
			if (!parent.inTable || (parent instanceof TableNode))
			{
				break;
			}
		} while ((parent = parent.parent) != null);

		assert (parent != null);

		return parent;
	}

	void setIndexField(Field indexField)
	{
		this.indexField = indexField;
	}

	int[] getIndex(Object obj, int ordinal)
	{
		if (indexField != null)
		{
			try
			{
				return SnmpUtils.getSnmpExtension(indexField.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e)
			{
				return new int[] { ordinal };
			}
		} else
		{
			return new int[] { ordinal };
		}
	}

	TableEntryNode getEntry()
	{
		return (TableEntryNode) snmpNodes.iterator().next();
	}
}
