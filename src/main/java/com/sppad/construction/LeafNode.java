package com.sppad.construction;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeafNode extends Node
{
  private static final Logger logger = LoggerFactory.getLogger(LeafNode.class);

  public LeafNode(final Field field, final Node parent)
  {
    super(field.getType(), parent, parent.inTable);

    logger.debug("Creating leaf for " + field.getName());

    this.name = field.getName();
    this.field = field;
  }

  public void accept(final INodeVisitor visitor)
  {
    visitor.visitEnter(this);
    visitor.visitExit(this);
  }

}
