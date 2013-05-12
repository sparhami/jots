package com.sppad.jots.construction;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

import org.snmp4j.smi.OID;

import com.google.common.base.Joiner;
import com.sppad.jots.JotsOID;
import com.sppad.jots.construction.mib.MibInfo;
import com.sppad.jots.construction.mib.TextualConvention;
import com.sppad.jots.construction.nodes.EntryNode;
import com.sppad.jots.construction.nodes.INodeVisitor;
import com.sppad.jots.construction.nodes.LeafNode;
import com.sppad.jots.construction.nodes.Node;
import com.sppad.jots.construction.nodes.RootNode;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;

public class MibGenerator implements INodeVisitor
{
	private static final Joiner joiner = Joiner.on("");

	static void printHeader(final String mibName, final String rootName,
			final String parentName, int oid, final PrintStream ps)
	{
		ps.print(MibInfo
				.createMibHeader(mibName, rootName, "", parentName, oid));
	}

	static void generateMib(Object obj, SnmpTreeBuilder treeBuilder,
			final String mibName, final String rootName,
			final String parentName, final OutputStream os)
	{
		final int[] prefix = treeBuilder.getPrefix();

		final RootNode node = NodeTreeConstructor.createTree(obj.getClass(),
				treeBuilder.getInclusionStrategy());

		final Map<Node, int[]> staticOidMap = OidGenerator
				.getStaticOidParts(node);

		final MibGenerator gen = new MibGenerator(prefix, staticOidMap);

		PrintStream ps = new PrintStream(os);
		printHeader(mibName, rootName, parentName, prefix[prefix.length - 1],
				ps);
		TextualConvention.addTextualConventions(node, ps);
		
		node.accept(gen);
	}

	private final Deque<String> constructedNameStack = new LinkedList<String>();

	private final Deque<String> nameStack = new LinkedList<String>();

	private final int[] prefix;

	private final Map<Node, int[]> staticOidMap;

	private MibGenerator(final int[] prefix, final Map<Node, int[]> staticOidMap)
	{
		this.staticOidMap = staticOidMap;
		this.prefix = prefix;
	}

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
		visitEnterNode(node, "Entry");
	}

	@Override
	public void visitEnter(final LeafNode node)
	{
		visitEnterNode(node, "");
	}

	@Override
	public void visitEnter(final RootNode node)
	{
		visitEnterNode(node, "");
	}

	@Override
	public void visitEnter(final TableEntryNode node)
	{
		visitEnterNode(node, "Entry");
	}

	@Override
	public void visitEnter(final TableNode node)
	{
		visitEnterNode(node, "Table");
	}

	public void visitEnterNode(final Node node, final String ending)
	{
		nameStack.addLast(node.name);

		final int[] staticOid = staticOidMap.get(node);
		final String name = constructName(ending);

		final OID oid = JotsOID.createOID(prefix, staticOid);
		printOid(name, oid);

		constructedNameStack.addLast(name);
	}

	@Override
	public void visitExit(final EntryNode node)
	{
		visitExitNode();
	}

	@Override
	public void visitExit(final LeafNode node)
	{
		visitExitNode();
	}

	@Override
	public void visitExit(final RootNode node)
	{
		visitExitNode();
	}

	@Override
	public void visitExit(final TableEntryNode node)
	{
		visitExitNode();
	}

	@Override
	public void visitExit(final TableNode node)
	{
		visitExitNode();
	}

	public void visitExitNode()
	{
		nameStack.removeLast();
		constructedNameStack.removeLast();
	}
}
