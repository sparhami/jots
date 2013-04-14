package com.sppad.jots.construction;

class RootNode extends InnerNode
{
  RootNode(final Class<?> klass)
  {
    super(klass, null, false);

    this.name = klass.getSimpleName();
  }

  void accept(final INodeVisitor visitor)
  {
    visitor.visitEnter(this);

    for (final Node child : snmpNodes)
      child.accept(visitor);

    visitor.visitExit(this);
  }
}
