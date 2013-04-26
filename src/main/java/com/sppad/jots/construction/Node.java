package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import com.sppad.jots.util.Fields;

abstract class Node
{
	static boolean isLeaf(final Class<?> cls)
	{
		return Fields.isSimple(cls);
	}

	/** The field that allows access to this */
	Field field;

	/** Whether or not the node is within a SNMP table */
	final boolean inTable;

	/** The class that this node corresponds to */
	final Class<?> klass;

	/** The name for this node */
	final String name;

	/** The children nodes */
	final Collection<Node> nodes = new LinkedList<Node>();

	/** The parent of this node, usually the object containing this node. */
	final Node parent;

	/** The children nodes in snmp order */
	final Collection<Node> snmpNodes = new LinkedList<Node>();

	/**
	 * The parent node for traversing the mib. This is diffrent than parent
	 * since nested collections need to be flattened into a mult-indexed table.
	 */
	final Node snmpParent;

	Node(final Class<?> klass, final Node parent, final boolean inTable,
			final String name)
	{
		this.klass = klass;
		this.parent = parent;
		this.snmpParent = getSnmpParentNode(parent);
		this.inTable = inTable;
		this.name = name;
	}

	/**
	 * Allows visiting the node using a hierarchical visitor pattern according
	 * to the SNMP ordering (nested collections in hierarchy flattened).
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

	Node getSnmpParentNode(final Node parent)
	{
		return parent;
	}
}
