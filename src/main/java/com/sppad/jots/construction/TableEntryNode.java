package com.sppad.jots.construction;

import java.lang.reflect.Field;

class TableEntryNode extends InnerNode
{
  public TableEntryNode(final Field field, final Class<?> cls, final Node parent)
  {
    super(cls, parent, parent.inTable);

    this.name = "";
    this.field = field;
  }

  public void accept(final INodeVisitor visitor)
  {
    visitor.visitEnter(this);

    for (final Node child : snmpNodes)
      child.accept(visitor);

    visitor.visitExit(this);
  }
}
