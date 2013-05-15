package com.sppad.jots.construction.nodes;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.Nullable;

import com.sppad.jots.util.Fields;
import com.sppad.jots.util.Strings;

public abstract class Node
{
	public static boolean isLeaf(final Class<?> cls)
	{
		return Fields.isSimple(cls);
	}

	public static boolean isTable(final Class<?> cls)
	{
		return Collection.class.isAssignableFrom(cls)
				|| Map.class.isAssignableFrom(cls);
	}

	/** The field that allows access to this */
	public final Field field;

	/** Whether or not the node is within a SNMP table */
	public final boolean inTable;

	/** The class that this node corresponds to */
	public final Class<?> klass;

	/** The name for this node */
	public final String name;

	/** The children nodes */
	public final Collection<Node> nodes = new LinkedList<Node>();

	/** The parent of this node, usually the object containing this node. */
	public final Node parent;

	/** The children nodes in snmp order */
	public final Collection<Node> snmpNodes = new LinkedList<Node>();

	/**
	 * The parent node for traversing the mib. This is diffrent than parent
	 * since nested collections need to be flattened into a mult-indexed table.
	 */
	public final Node snmpParent;

	Node(final Class<?> klass, @Nullable final Node parent,
			final boolean inTable, final String name,
			@Nullable final Field field)
	{
		this.klass = klass;
		this.parent = parent;
		this.snmpParent = getSnmpParentNode(parent);
		this.inTable = inTable;
		this.name = Strings.firstCharToUppercase(name);
		this.field = field;

		if (field != null)
			field.setAccessible(true);
	}

	/**
	 * Allows visiting the node using a hierarchical visitor pattern according
	 * to the SNMP ordering (nested collections in hierarchy flattened).
	 */
	abstract void accept(final INodeVisitor visitor);

	public void addChild(final Node node)
	{
		nodes.add(node);
	}

	public void addSnmpChild(final Node node)
	{
		snmpNodes.add(node);
	}

	public String getEnding() {
		return "";
	}
	
	Node getSnmpParentNode(final Node parent)
	{
		return parent;
	}
}
