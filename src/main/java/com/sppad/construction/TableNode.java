package com.sppad.construction;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sppad.snmp.annotations.Jots;

public class TableNode extends InnerNode
{
  private static final Logger logger = LoggerFactory.getLogger(TableNode.class);
  
  public final  Class<?> entryClass;
  
  public TableNode(final Field field, final Node parent)
  {
    super(field.getAnnotation(Jots.class).cls(), parent, true);
    
    logger.debug("Creating table for " + field.getName());
    
    this.name = field.getName();
    this.entryClass = field.getAnnotation(Jots.class).cls();
    this.field = field;
  }
  
  public void accept(final INodeVisitor visitor) {
    visitor.visitEnter(this);
    
    for(final Node child : snmpNodes)
      child.accept(visitor);
    
    visitor.visitExit(this);
  }
}
