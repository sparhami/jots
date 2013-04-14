package com.sppad.jots.construction;

import java.lang.reflect.Field;

class LeafNode extends Node
{
  public LeafNode(final Field field, final Node parent)
  {
    super(field.getType(), parent, parent.inTable);

    this.name = field.getName();
    this.field = field;
  }

  public void accept(final INodeVisitor visitor)
  {
    visitor.visitEnter(this);
    visitor.visitExit(this);
  }

}
