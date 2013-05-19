package com.sppad.jots.construction.nodes;


public class RootNode extends InnerNode
{
	public RootNode(final Class<?> klass, final String name)
	{
		super(klass, null, false, name, null);
	}

	public void accept(final INodeVisitor visitor)
	{
		visitor.visitEnter(this);

		for (final Node child : snmpNodes)
			child.accept(visitor);

		visitor.visitExit(this);
	}
}
