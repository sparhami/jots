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
import com.sppad.jots.construction.nodes.LeafNode;
import com.sppad.jots.construction.nodes.Node;
import com.sppad.jots.construction.nodes.RootNode;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;
import com.sppad.jots.datastructures.primative.IntStack;
import com.sppad.jots.log.ErrorMessage;
import com.sppad.jots.lookup.SnmpLookupField;

class SnmpTreeConstructor {
	private static final Comparator<SnmpLookupField> COMPARE_BY_OID = new Comparator<SnmpLookupField>() {
		public int compare(final SnmpLookupField arg0,
				final SnmpLookupField arg1) {
			return arg0.getOid().compareTo(arg1.getOid());
		}
	};

	private static final Logger logger = LoggerFactory
			.getLogger(SnmpTreeConstructor.class);

	static SnmpTree create(final Object obj, final SnmpTreeBuilder treeBuilder) {
		RootNode node = NodeTreeConstructor.createTree(obj.getClass(),
				treeBuilder.getInclusionStrategy());

		SnmpTreeConstructor tc = new SnmpTreeConstructor(
				treeBuilder.getPrefix());

		tc.descend(node, obj);

		return new SnmpTree(tc.prefix, tc.sortedSet);
	}

	private static Collection<?> getCollection(final Object obj) {
		if (obj instanceof Map)
			return ((Map<?, ?>) obj).values();
		else
			return (Collection<?>) obj;
	}

	/* Tracks the length of each index pushed onto the stack */
	private final IntStack indexLengthStack = new IntStack();

	/** Tracks the table indices */
	private final IntStack indexStack = new IntStack();

	/** The prefix for all OIDs */
	private final int[] prefix;

	/** The SnmpLookupFields that make up the generated SnmpTree */
	private final SortedSet<SnmpLookupField> sortedSet = new TreeSet<SnmpLookupField>(
			COMPARE_BY_OID);

	private SnmpTreeConstructor(int[] prefix) {
		this.prefix = prefix;
	}

	private void addToSnmpTree(final LeafNode node, final Object obj) {

		final int[] oidArray = (int[]) node.getProperty("OID");
		final OID oid = JotsOID.createTerminalOID(prefix, oidArray, indexStack);

		addToSnmpTree(oid, node.field, obj);
	}

	private void addToSnmpTree(final OID oid, final Field field,
			final Object object) {
		sortedSet.add(SnmpLookupField.create(oid, field, object));
	}

	private void descend(final Node node, final Object obj) {
		for (final Node child : node.nodes)
			if (child instanceof TableNode)
				descendIntoCollection((TableNode) child, obj);
			else
				descendIntoObject(child, obj);
	}

	private void descendIntoCollection(final TableNode node, final Object obj) {
		try {
			final TableEntryNode child = node.getEntry();
			final Object tableObject = node.field.get(obj);

			descendIntoCollection(child, getCollection(tableObject));
		} catch (IllegalAccessException e) {
			logger.warn(
					ErrorMessage.CANNOT_CREATE_SUBTREE_DUE_TO_ACCESS.getFmt(),
					node.field);
		}
	}

	private void descendIntoCollection(final TableEntryNode node,
			final Collection<?> collection) {
		int index = 1;
		for (final Object next : collection) {
			pushExtension(node, next, index++);
			descend(node, next);
			popExtension();
		}
	}

	private void descendIntoObject(final Node node, final Object obj) {
		try {
			if (node instanceof LeafNode)
				addToSnmpTree((LeafNode) node, obj);
			else
				descend(node, node.field.get(obj));
		} catch (IllegalAccessException e) {
			logger.warn(
					ErrorMessage.CANNOT_CREATE_SUBTREE_DUE_TO_ACCESS.getFmt(),
					node.field);
		}
	}

	private void popExtension() {
		indexStack.remove(indexLengthStack.pop());
	}

	private void pushExtension(final TableEntryNode node, final Object next,
			final int index) {
		final int[] extension = node.getIndex(next, index);

		indexLengthStack.push(extension.length);

		for (final int part : extension)
			indexStack.push(part);
	}
}
