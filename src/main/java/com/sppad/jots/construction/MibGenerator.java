package com.sppad.jots.construction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map;

import org.snmp4j.smi.OID;

import com.google.common.base.Joiner;
import com.sppad.jots.JotsOID;
import com.sppad.jots.SnmpTree;
import com.sppad.jots.construction.mib.MibConstructor;
import com.sppad.jots.datastructures.primative.IntStack;

public class MibGenerator implements INodeVisitor
{
	private static final Joiner joiner = Joiner.on("");

	private static String firstCharToUppercase(final String string)
	{

		final StringBuilder builder = new StringBuilder(string);
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

		return builder.toString();
	}

	private final LinkedList<String> constructedNameStack = new LinkedList<String>();

	private final MibConstructor constructor;

	private final IntStack extensionStack = new IntStack();

	private final LinkedList<String> nameStack = new LinkedList<String>();

	private final int[] prefix;

	private final Map<Node, IntStack> staticOidMap;

	private String constructName(String ending)
	{
		return joiner.join(nameStack) + ending;
	}

	private void printOid(final String nameString, OID oid)
	{
		System.out.printf("%-20s %-20s\n", oid, nameString);
	}

	@Override
	public void visitEnter(final EntryNode node)
	{
		nameStack.addLast(firstCharToUppercase(node.name));

		final IntStack staticOid = staticOidMap.get(node);
		final String name = constructName("Entry");
		final String parentName = constructedNameStack.peek();

		final OID oid = JotsOID.createOID(prefix, staticOid);
		printOid(name, oid);

		constructedNameStack.addLast(name);
	}

	@Override
	public void visitEnter(final LeafNode node)
	{
		nameStack.addLast(firstCharToUppercase(node.name));

		final IntStack staticOid = staticOidMap.get(node);
		final String name = constructName("");
		final String parentName = constructedNameStack.peek();

		final OID oid = JotsOID.createTerminalOID(prefix, staticOid,
				extensionStack);

		printOid(name, oid);
	}

	@Override
	public void visitEnter(final RootNode node)
	{
		nameStack.addLast(firstCharToUppercase(node.name));

		final IntStack staticOid = staticOidMap.get(node);
		final String name = constructName("");

		final OID oid = JotsOID.createOID(prefix, staticOid);
		printOid(name, oid);

		constructedNameStack.addLast(name);
	}

	@Override
	public void visitEnter(final TableEntryNode node)
	{
		final IntStack staticOid = staticOidMap.get(node);
		final String name = constructName("Entry");
		final String parentName = constructedNameStack.peek();

		final OID oid = JotsOID.createOID(prefix, staticOid);
		printOid(name, oid);

		constructedNameStack.addLast(name);
	}

	@Override
	public void visitEnter(final TableNode node)
	{
		nameStack.addLast(firstCharToUppercase(node.name));

		final IntStack staticOid = staticOidMap.get(node);
		final String name = constructName("Table");
		final String parentName = constructedNameStack.peek();

		final OID oid = JotsOID.createOID(prefix, staticOid);
		printOid(name, oid);

		constructedNameStack.addLast(name);
	}

	@Override
	public void visitExit(final EntryNode node)
	{
		nameStack.removeLast();
		constructedNameStack.removeLast();
	}

	@Override
	public void visitExit(final LeafNode node)
	{
		nameStack.removeLast();
	}

	@Override
	public void visitExit(final RootNode node)
	{
		nameStack.removeLast();
		constructedNameStack.removeLast();
	}

	@Override
	public void visitExit(final TableEntryNode node)
	{
		constructedNameStack.removeLast();
	}

	@Override
	public void visitExit(final TableNode node)
	{
		nameStack.removeLast();
		constructedNameStack.removeLast();
	}

	private MibGenerator(final int[] prefix,
			final Map<Node, IntStack> staticOidMap,
			final MibConstructor constructor)
	{
		this.staticOidMap = staticOidMap;
		this.prefix = prefix;
		this.constructor = constructor;
	}

	public static void generateMib(Object obj, TreeBuilder treeBuilder,
									final String mibName,
									final String rootName,
									final String parentName,
									final OutputStream os)
	{
		final int[] prefix = treeBuilder.getPrefix();

		final Node node = NodeTreeConstructor.createTree(obj.getClass(),
				treeBuilder.getInclusionStrategy());

		final Map<Node, IntStack> staticOidMap = OidGenerator
				.getStaticOidParts(node);

		final MibConstructor constructor = new MibConstructor(mibName,
				rootName, parentName, prefix[prefix.length - 1], os);

		final MibGenerator gen = new MibGenerator(prefix, staticOidMap,
				constructor);

		node.accept(gen);

		try
		{
			constructor.finish();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
