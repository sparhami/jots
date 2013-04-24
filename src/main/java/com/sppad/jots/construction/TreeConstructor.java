package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.snmp4j.smi.OID;

import com.sppad.jots.JotsOID;
import com.sppad.jots.SnmpTree;
import com.sppad.jots.datastructures.primative.IntStack;
import com.sppad.jots.lookup.SnmpLookupField;
import com.sppad.jots.util.FieldUtils;

class TreeConstructor
{
	private static final Comparator<SnmpLookupField> COMPARE_BY_OID = new Comparator<SnmpLookupField>()
	{
		public int compare(SnmpLookupField arg0, SnmpLookupField arg1)
		{
			return arg0.getOid().compareTo(arg1.getOid());
		}
	};

	public static SnmpTree create(Object obj, TreeBuilder treeBuilder)
	{
		Node node = NodeTreeConstructor.createTree(obj.getClass(),
				treeBuilder.getInclusionStrategy());
		Map<Node, IntStack> staticOidMap = OidGenerator.getStaticOidParts(node);

		TreeConstructor tc = new TreeConstructor(treeBuilder.getPrefix(),
				staticOidMap);
		tc.descend(node, obj);

		return new SnmpTree(tc.prefix, tc.sortedSet);
	}

	private final IntStack extensionStack = new IntStack();

	private final int[] prefix;

	private final SortedSet<SnmpLookupField> sortedSet = new TreeSet<SnmpLookupField>(
			COMPARE_BY_OID);

	private final Map<Node, IntStack> staticOidMap;

	private TreeConstructor(int[] prefix, Map<Node, IntStack> staticOidMap)
	{
		this.prefix = prefix;
		this.staticOidMap = staticOidMap;
	}

	private void add(final OID oid, final Field field, final Object object)
	{
		sortedSet.add(SnmpLookupField.create(oid, field, object));
	}

	private void addToSnmpTree(LeafNode node, Object obj)
	{
		final OID oid = JotsOID.createTerminalOID(prefix,
				staticOidMap.get(node), extensionStack);

		add(oid, node.field, obj);
	}

	private void descend(Node node, Object obj)
	{
		for (Node child : node.nodes)
		{
			if (child instanceof TableNode)
			{
				handleCollection((TableNode) child, obj);
			} else
			{
				handleObject(child, obj);
			}
		}
	}

	private void handleCollection(TableNode node, Object obj)
	{
		try
		{
			Node child = node.snmpNodes.iterator().next();
			assert (child instanceof TableEntryNode);

			Field field = node.field;
			field.setAccessible(true);

			Object tableObject = field.get(obj);

			Collection<?> collection;

			if (tableObject instanceof Map)
				collection = ((Map<?, ?>) tableObject).values();
			else
				collection = (Collection<?>) tableObject;

			int index = 1;
			extensionStack.push(0);

			for (Object next : collection)
			{
				extensionStack.pop();
				extensionStack.push(index++);

				descend(child, next);
			}

			extensionStack.pop();

		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

	}

	private void handleObject(Node node, Object obj)
	{
		try
		{
			Field field = node.field;
			field.setAccessible(true);

			if (node instanceof LeafNode)
			{
				addToSnmpTree((LeafNode) node, obj);
			} else
			{
				descend(node, field.get(obj));
			}
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
}
