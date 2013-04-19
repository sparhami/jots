package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import com.sppad.jots.annotations.Jots;
import com.sppad.jots.util.FieldUtils;

abstract class Node
{
  static boolean isLeaf(Class<?> cls)
  {
    return FieldUtils.isSimple(cls);
  }

  static boolean isCollection(Field field)
  {
    return Collection.class.isAssignableFrom(field.getType());
  }

  static boolean hasCollectionAnnotation(Field field)
  {
    return field.getAnnotation(Jots.class) != null;
  }

  /** The field that allows access to this */
  Field field;

  /** Whether or not the node is within a SNMP table */
  final boolean inTable;

  /** The class that this node corresponds to */
  final Class<?> klass;

  /** The name for this node */
  String name;

  /** The children nodes */
  final Collection<Node> nodes = new LinkedList<Node>();

  /** The parent of this node, usually the object containing this node. */
  final Node parent;

  /** The children nodes in snmp order */
  final Collection<Node> snmpNodes = new LinkedList<Node>();

  /**
   * The parent node for traversing the mib. This is diffrent than parent since
   * nested collections need to be flattened into a mult-indexed table.
   */
  final Node snmpParent;

  protected Node(final Class<?> klass, final Node parent, final boolean inTable)
  {
    this.klass = klass;
    this.parent = parent;
    this.snmpParent = getSnmpParentNode(parent);
    this.inTable = inTable;
  }

  Node getSnmpParentNode(final Node parent)
  {
    return parent;
  }

  /**
   * Allows visiting the node using a hierarchical visitor pattern according to
   * the SNMP ordering (nested collections in hierarchy flattened).
   */
  abstract void accept(final INodeVisitor visitor);

  void addChild(final Node node)
  {
    nodes.add(node);
  }

  void addSnmpChild(final Node node)
  {
    snmpNodes.add(node);
  }
}
