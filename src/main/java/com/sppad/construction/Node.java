package com.sppad.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import com.sppad.snmp.annotations.Jots;
import com.sppad.snmp.util.SnmpUtils;

public abstract class Node
{
  public static boolean isLeaf(Class<?> cls)
  {
    return SnmpUtils.isSimple(cls);
  }

  public static boolean isTable(Field field)
  {
    return Collection.class.isAssignableFrom(field.getType()) &&
        field.getAnnotation(Jots.class) != null;
  }

  /** The field that allows access to this */
  protected Field field;

  /** Whether or not the node is within a SNMP table */
  public final boolean inTable;

  /** The class that this node corresponds to */
  public final Class<?> klass;

  /** The name for this node */
  public String name;

  /** The children nodes */
  public final Collection<Node> nodes = new LinkedList<Node>();

  /** The parent of this node, usually the object containing this node. */
  public final Node parent;

  /** The children nodes in snmp order */
  public final Collection<Node> snmpNodes = new LinkedList<Node>();

  /**
   * The parent node for traversing the mib. This is diffrent than parent since
   * nested collections need to be flattened into a mult-indexed table.
   */
  public final Node snmpParent;

  protected Node(final Class<?> klass, final Node parent, final boolean inTable)
  {
    this.klass = klass;
    this.parent = parent;
    this.snmpParent = getSnmpParentNode(parent);
    this.inTable = inTable;
  }

  public Node getSnmpParentNode(final Node parent)
  {
    return parent;
  }

  /**
   * Allows visiting the node using a hierarchical visitor pattern.
   */
  public abstract void accept(final INodeVisitor visitor);

  public void addChild(final Node node)
  {
    nodes.add(node);
  }

  public void addSnmpChild(final Node node)
  {
    snmpNodes.add(node);
  }
}
