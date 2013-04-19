package com.sppad.jots.construction;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.sppad.jots.log.Messages;

public class NodeTreeConstructor implements INodeVisitor
{
  private static final String COLLECTION_NO_ANNOTATION = Messages
      .getString("COLLECTION_NO_ANNOTATION");

  private static final Logger logger = LoggerFactory
      .getLogger(NodeTreeConstructor.class);

  public static Node createTree(
      final Class<?> cls,
      final Predicate<Field> inclusionStrategy)
  {
    final Node root = new RootNode(cls);
    final NodeTreeConstructor constructor = new NodeTreeConstructor(
        inclusionStrategy);

    root.accept(constructor);

    return root;
  }

  private final Predicate<Field> inclusionStrategy;

  private NodeTreeConstructor(final Predicate<Field> inclusionStrategy)
  {
    this.inclusionStrategy = inclusionStrategy;
  }

  private void addChild(final TableNode parent)
  {
    final Node child = new TableEntryNode(parent.field, parent.entryClass,
        parent);

    child.parent.addChild(child);
    child.snmpParent.addSnmpChild(child);
  }

  private void addChildren(final InnerNode parent)
  {
    for (final Field field : parent.fields)
    {
      if (!include(field))
        continue;

      final Node child = createNode(field, parent);

      child.parent.addChild(child);
      child.snmpParent.addSnmpChild(child);
    }
  }

  private Node createNode(final Field field, final Node parent)
  {
    final Node node;

    if (Node.isTable(field.getType()))
    {
      node = new TableNode(field, parent);
    }
    else if (Node.isLeaf(field.getType()))
    {
      node = new LeafNode(field, parent);
    }
    else
    {
      node = new EntryNode(field, parent);
    }

    return node;
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

  @Override
  public void visitEnter(EntryNode node)
  {
    addChildren(node);
  }

  @Override
  public void visitEnter(LeafNode node)
  {

  }

  @Override
  public void visitEnter(RootNode node)
  {
    addChildren(node);
  }

  @Override
  public void visitEnter(TableEntryNode node)
  {
    addChildren(node);
  }

  @Override
  public void visitEnter(TableNode node)
  {
    addChild(node);
  }

  @Override
  public void visitExit(EntryNode node)
  {

  }

  @Override
  public void visitExit(LeafNode node)
  {

  }

  @Override
  public void visitExit(RootNode node)
  {

  }

  @Override
  public void visitExit(TableEntryNode node)
  {

  }

  @Override
  public void visitExit(TableNode node)
  {

  }

}
