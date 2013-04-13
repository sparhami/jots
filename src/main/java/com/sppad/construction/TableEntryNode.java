package com.sppad.construction;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableEntryNode extends InnerNode
{
  private static final Logger logger = LoggerFactory
      .getLogger(TableEntryNode.class);

  public TableEntryNode(final Field field, final Class<?> cls, final Node parent)
  {
    super(cls, parent, parent.inTable);

    logger.debug("Creating table entry for " + field.getName());

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

  @Override
  public Node getSnmpParentNode(Node parent)
  {
    for (; parent != null; parent = parent.parent)
      if (!parent.inTable || (parent instanceof TableNode))
        break;
        
    return parent;
  }
}
