package com.sppad.construction;

import java.lang.reflect.Field;

public class Constructor
{
  public static RootNode create(final Class<?> cls)
  {
    final RootNode root = new RootNode(cls);
    Constructor.createSubtree(root);

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

  private static void createSubtree(final InnerNode node)
  {
    if (node instanceof TableNode)
    {
      final TableNode table = (TableNode) node;

      final TableEntryNode child = new TableEntryNode(table.field,
          table.entryClass, table);

      child.parent.addChild(child);
      child.snmpParent.addSnmpChild(child);

      createSubtree(child);
    }
    else
    {
      for (final Field field : node.fields)
      {
        final Node child = createNode(field, node);

        child.parent.addChild(child);
        child.snmpParent.addSnmpChild(child);

        if (child instanceof InnerNode)
          createSubtree((InnerNode) child);
      }
    }
  }
}
