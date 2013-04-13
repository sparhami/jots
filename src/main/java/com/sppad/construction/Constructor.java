package com.sppad.construction;

import java.lang.reflect.Field;

public class Constructor implements INodeVisitor
{
  private static void addChild(final TableNode node)
  {
    final Node child = new TableEntryNode(node.field, node.entryClass, node);

    child.parent.addChild(child);
    child.snmpParent.addSnmpChild(child);
  }

  private static void addChildren(final InnerNode node)
  {
    for (final Field field : node.fields)
    {
      final Node child = createNode(field, node);

      child.parent.addChild(child);
      child.snmpParent.addSnmpChild(child);
    }
  }

  public static RootNode create(final Class<?> cls)
  {
    final RootNode root = new RootNode(cls);
    root.accept(new Constructor());

    return root;
  }

  private static Node createNode(final Field field, final Node parent)
  {
    if (Node.isTable(field))
      return new TableNode(field, parent);
    else if (Node.isLeaf(field.getType()))
      return new LeafNode(field, parent);
    else
      return new EntryNode(field, parent);
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
