package com.sppad.jots.construction;

import java.lang.reflect.Field;

import com.sppad.jots.annotations.Jots;

class TableNode extends InnerNode
{
  public final Class<?> entryClass;

  public TableNode(final Field field, final Node parent)
  {
    super(field.getAnnotation(Jots.class).cls(), parent, true);

    this.name = field.getName();
    this.entryClass = field.getAnnotation(Jots.class).cls();
    this.field = field;
  }

  public void accept(final INodeVisitor visitor)
  {
    visitor.visitEnter(this);

    for (final Node child : snmpNodes)
      child.accept(visitor);

    visitor.visitExit(this);
  }

  @Override
  public Node getSnmpParentNode(Node parent)
  {
    do
    {
      // Note: the root node will always have inTable = false
      if (!parent.inTable || (parent instanceof TableNode))
      {
        break;
      }
    }
    while ((parent = parent.parent) != null);

    assert (parent != null);

    return parent;
  }
}
