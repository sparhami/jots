package com.sppad.jots.construction;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sppad.jots.log.Messages;

public class Constructor
{
  private static class NodeVisitor implements INodeVisitor
  {

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

  private static final Logger logger = LoggerFactory
      .getLogger(Constructor.class);

  private static void addChild(final TableNode parent)
  {
    final Node child = new TableEntryNode(parent.field, parent.entryClass,
        parent);

    child.parent.addChild(child);
    child.snmpParent.addSnmpChild(child);
  }

  private static void addChildren(final InnerNode parent)
  {
    for (final Field field : parent.fields)
    {
      final Node child = createNode(field, parent);

      if (child == null)
        continue;

      child.parent.addChild(child);
      child.snmpParent.addSnmpChild(child);
    }
  }

  public static Node construct(final Class<?> cls)
  {
    final Node root = new RootNode(cls);
    root.accept(new NodeVisitor());

    return root;
  }

  private static final String COLLECTION_NO_ANNOTATION = Messages
      .getString("COLLECTION_NO_ANNOTATION");

  private static Node createNode(final Field field, final Node parent)
  {
    final Node node;

    boolean leaf = Node.isLeaf(field.getType());
    boolean collection = Node.isCollection(field);
    boolean annotation = Node.hasCollectionAnnotation(field);

    if (collection && !annotation)
    {
      logger.warn(COLLECTION_NO_ANNOTATION, parent.klass, field.getName());

      node = null;
    }
    else if (collection)
    {
      node = new TableNode(field, parent);
    }
    else if (leaf)
    {
      node = new LeafNode(field, parent);
    }
    else
    {
      node = new EntryNode(field, parent);
    }

    return node;
  }
}
