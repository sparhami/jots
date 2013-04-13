package com.sppad.construction;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryNode extends InnerNode
{
  private static final Logger logger = LoggerFactory.getLogger(EntryNode.class);

  public EntryNode(final Field field, final Node parent)
  {
    super(field.getType(), parent, parent.inTable);

    logger.debug("Creating entry for " + field.getName());

    this.name = field.getName();
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
