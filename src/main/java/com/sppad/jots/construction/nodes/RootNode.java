package com.sppad.jots.construction.nodes;


public class RootNode extends InnerNode
{
	public RootNode(final Class<?> klass)
	{
		super(klass, null, false, klass.getSimpleName(), null);
	}

	public void accept(final INodeVisitor visitor)
	{
		visitor.visitEnter(this);

		for (final Node child : snmpNodes)
			child.accept(visitor);

		visitor.visitExit(this);
	}
}
