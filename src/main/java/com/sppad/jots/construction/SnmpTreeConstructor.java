package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import com.sppad.jots.JotsOID;
import com.sppad.jots.SnmpTree;
import com.sppad.jots.datastructures.primative.IntStack;
import com.sppad.jots.log.ErrorMessage;
import com.sppad.jots.lookup.SnmpLookupField;

class SnmpTreeConstructor
{
	private static final Comparator<SnmpLookupField> COMPARE_BY_OID = new Comparator<SnmpLookupField>()
	{
		public int compare(final SnmpLookupField arg0,
				final SnmpLookupField arg1)
		{
			return arg0.getOid().compareTo(arg1.getOid());
		}
	};

	private static final Logger logger = LoggerFactory
			.getLogger(SnmpTreeConstructor.class);

	static SnmpTree create(final Object obj,
			final SnmpTreeBuilder treeBuilder)
	{
		Node node = NodeTreeConstructor.createTree(obj.getClass(),
				treeBuilder.getInclusionStrategy());
		Map<Node, int[]> staticOidMap = OidGenerator.getStaticOidParts(node);

		SnmpTreeConstructor tc = new SnmpTreeConstructor(treeBuilder.getPrefix(),
				staticOidMap);
		tc.descend(node, obj);

		return new SnmpTree(tc.prefix, tc.sortedSet);
	}

	private static Collection<?> getCollection(final Object obj)
	{
		if (obj instanceof Map)
			return ((Map<?, ?>) obj).values();
		else
			return (Collection<?>) obj;
	}

	private final IntStack extensionLengthStack = new IntStack();

	private final IntStack extensionStack = new IntStack();

	private final int[] prefix;

	private final SortedSet<SnmpLookupField> sortedSet = new TreeSet<SnmpLookupField>(
			COMPARE_BY_OID);

	private final Map<Node, int[]> staticOidMap;

	private SnmpTreeConstructor(int[] prefix, final Map<Node, int[]> staticOidMap)
	{
		this.prefix = prefix;
		this.staticOidMap = staticOidMap;
	}

	private void add(final OID oid, final Field field, final Object object)
	{
		sortedSet.add(SnmpLookupField.create(oid, field, object));
	}

	private void addExtension(final TableNode node, final Object next,
			final int index)
	{
		final int[] extension = node.getIndex(next, index);

		extensionLengthStack.push(extension.length);

		for (final int part : extension)
			extensionStack.push(part);
	}

	private void addToSnmpTree(final LeafNode node, final Object obj)
	{
		final OID oid = JotsOID.createTerminalOID(prefix,
				staticOidMap.get(node), extensionStack);

		add(oid, node.field, obj);
	}

	private void descend(final Node node, final Object obj)
	{
		for (final Node child : node.nodes)
			if (child instanceof TableNode)
				handleCollection((TableNode) child, obj);
			else
				handleObject(child, obj);
	}

	private void handleCollection(final TableNode node, final Object obj)
	{
		try
		{
			final TableEntryNode child = node.getEntry();
			final Object tableObject = node.field.get(obj);

			int index = 1;
			for (final Object next : getCollection(tableObject))
			{
				addExtension(node, next, index++);
				descend(child, next);
				removeExtension();
			}

		} catch (IllegalAccessException e)
		{
			logger.warn(
					ErrorMessage.CANNOT_CREATE_SUBTREE_DUE_TO_ACCESS.getFmt(),
					node.field);
			e.printStackTrace();
		}
	}

	private void handleObject(final Node node, final Object obj)
	{
		try
		{
			if (node instanceof LeafNode)
			{
				addToSnmpTree((LeafNode) node, obj);
			} else
			{
				descend(node, node.field.get(obj));
			}
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private void removeExtension()
	{
		extensionStack.remove(extensionLengthStack.pop());
	}
}
