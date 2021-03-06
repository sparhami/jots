package com.sppad.jots.construction.nodes;

import java.lang.reflect.Field;

import com.sppad.jots.util.SnmpUtils;

public class TableEntryNode extends InnerNode
{
	private Field indexField;

	public TableEntryNode(final Field field, final Class<?> cls,
			final Node parent)
	{
		super(cls, parent, parent.inTable, "", field);
	}

	@Override
	public String getEnding()
	{
		return "Entry";
	}

	public int[] getIndex(Object obj, int ordinal)
	{
		if (indexField != null)
		{
			try
			{
				return SnmpUtils.getSnmpExtension(indexField.get(obj));
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				return new int[] { ordinal };
			}
		}
		else
		{
			return new int[] { ordinal };
		}
	}

	public void setIndexField(Field indexField)
	{
		this.indexField = indexField;
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
