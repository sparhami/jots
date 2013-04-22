package com.sppad.jots.construction;

import java.lang.reflect.Field;

class EntryNode extends InnerNode
{
  EntryNode(final Field field, final Node parent)
  {
    super(field.getType(), parent, parent.inTable, field.getName());

    this.field = field;
  }

  void accept(final INodeVisitor visitor)
  {
    visitor.visitEnter(this);

    for (final Node child : snmpNodes)
      child.accept(visitor);

    visitor.visitExit(this);
  }
}
