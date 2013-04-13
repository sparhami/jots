package com.sppad.construction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootNode extends InnerNode
{
  private static final Logger logger = LoggerFactory.getLogger(RootNode.class);
  
  
  public RootNode(final Class<?> klass)
  {
    super(klass, null, false);
    
    logger.debug("Creating root node");
    
    this.name = klass.getSimpleName();
  }
  
  public void accept(final INodeVisitor visitor) {
    visitor.visitEnter(this);
    
    for(final Node child : snmpNodes)
      child.accept(visitor);
    
    visitor.visitExit(this);
  }
}
