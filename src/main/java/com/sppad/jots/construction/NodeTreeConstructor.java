package com.sppad.jots.construction;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.sppad.jots.log.Messages;

class NodeTreeConstructor
{
  private static final String COLLECTION_NO_ANNOTATION = Messages
      .getString("COLLECTION_NO_ANNOTATION");

  private static final Logger logger = LoggerFactory
      .getLogger(NodeTreeConstructor.class);

  static Node createTree(
      final Class<?> cls,
      final Predicate<Field> inclusionStrategy)
  {
    final RootNode root = new RootNode(cls);
    final NodeTreeConstructor constructor = new NodeTreeConstructor(
        inclusionStrategy);

    constructor.addChildren(root);

    return root;
  }

  private final Predicate<Field> inclusionStrategy;

  private NodeTreeConstructor(final Predicate<Field> inclusionStrategy)
  {
    this.inclusionStrategy = inclusionStrategy;
  }

  private void addChildren(final InnerNode parent)
  {
    for (final Field field : parent.fields)
    {
      if (!include(field))
        continue;

      final Node child;
      final Class<?> fieldType = field.getType();

      if (Node.isTable(fieldType))
      {
        child = new TableNode(field, parent);
        addTableChild((TableNode) child);
      }
      else if (Node.isLeaf(fieldType))
      {
        child = new LeafNode(field, parent);
      }
      else
      {
        child = new EntryNode(field, parent);
        addChildren((EntryNode) child);
      }

      child.parent.addChild(child);
      child.snmpParent.addSnmpChild(child);
    }
  }

  private void addTableChild(final TableNode parent)
  {
    final TableEntryNode child = new TableEntryNode(parent.field,
        parent.entryClass, parent);

    child.parent.addChild(child);
    child.snmpParent.addSnmpChild(child);

    addChildren(child);
  }

  private boolean include(final Field field)
  {
    final boolean leaf = Node.isLeaf(field.getType());
    final boolean collection = Node.isTable(field.getType());
    final boolean typeAnnotation = Node.hasCollectionAnnotation(field);

    if (collection && !typeAnnotation)
    {
      logger.warn(COLLECTION_NO_ANNOTATION, field.getDeclaringClass(),
          field.getName());
      return false;
    }
    else
    {
      return leaf || inclusionStrategy.apply(field);
    }
  }
}
