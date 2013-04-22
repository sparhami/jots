package com.sppad.jots.construction;

import java.lang.reflect.Field;

class LeafNode extends Node
{
  LeafNode(final Field field, final Node parent)
  {
    super(field.getType(), parent, parent.inTable, field.getName());

    this.field = field;
  }

  void accept(final INodeVisitor visitor)
  {
    visitor.visitEnter(this);
    visitor.visitExit(this);
  }

}
