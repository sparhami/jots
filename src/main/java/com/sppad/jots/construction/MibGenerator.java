package com.sppad.jots.construction;

import java.io.OutputStream;
import java.io.PrintStream;

import com.sppad.jots.construction.mib.MibEntry;
import com.sppad.jots.construction.mib.MibInfo;
import com.sppad.jots.construction.mib.MibLeaf;
import com.sppad.jots.construction.mib.MibTable;
import com.sppad.jots.construction.mib.TextualConvention;
import com.sppad.jots.construction.nodes.EntryNode;
import com.sppad.jots.construction.nodes.INodeVisitor;
import com.sppad.jots.construction.nodes.LeafNode;
import com.sppad.jots.construction.nodes.RootNode;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;

/** Generates a MIB coressponding to a Node tree. */
class MibGenerator
{
	private static class MibGeneratingVisitor implements INodeVisitor
	{
		private final PrintStream ps;

		private MibGeneratingVisitor(final int[] prefix, final PrintStream ps)
		{
			this.ps = ps;
		}

		@Override
		public void visitEnter(final EntryNode node)
		{
			MibEntry.print(node, ps);
		}

		@Override
		public void visitEnter(final LeafNode node)
		{
			final String parentName = (String) node.snmpParent
					.getProperty("NAME");
			final String name = (String) node.getProperty("NAME");
			final int[] staticOid = (int[]) node.getProperty("OID");

			MibLeaf.print(name, parentName,
					staticOid[staticOid.length - 1], node.klass, "", true, ps);
		}

		@Override
		public void visitEnter(final RootNode node)
		{

		}

		@Override
		public void visitEnter(final TableEntryNode node)
		{
			MibEntry.print(node, ps);
		}

		@Override
		public void visitEnter(final TableNode node)
		{
			final String parentName = (String) node.snmpParent
					.getProperty("NAME");
			final String name = (String) node.getProperty("NAME");
			final String childName = (String) node.getEntry().getProperty(
					"NAME");
			final int[] staticOid = (int[]) node.getProperty("OID");

			MibTable.print(node, childName, name, parentName,
					staticOid[staticOid.length - 1], ps);
		}

		@Override
		public void visitExit(final EntryNode node)
		{

		}

		@Override
		public void visitExit(final LeafNode node)
		{

		}

		@Override
		public void visitExit(final RootNode node)
		{

		}

		@Override
		public void visitExit(final TableEntryNode node)
		{

		}

		@Override
		public void visitExit(final TableNode node)
		{

		}
	}

	static void generateMib(Object obj, SnmpTreeBuilder treeBuilder,
			final String mibName, final String rootName,
			final String parentName, final OutputStream os)
	{
		final int[] prefix = treeBuilder.getPrefix();

		final RootNode node = NodeTreeConstructor.createTree(obj.getClass(),
				treeBuilder.getInclusionStrategy(), rootName);

		OidAssigner.tag(node);
		NameAssigner.tag(node);

		PrintStream ps = new PrintStream(os);
		printHeader(mibName, node.name, parentName, prefix[prefix.length - 1],
				ps);

		TextualConvention.addTextualConventions(node, ps);

		final MibGeneratingVisitor gen = new MibGeneratingVisitor(prefix, ps);
		node.accept(gen);

		printFooter(ps);
	}

	private static void printFooter(final PrintStream ps)
	{
		MibInfo.printFooter(ps);
	}

	private static void printHeader(final String mibName,
			final String rootName, final String parentName, int oid,
			final PrintStream ps)
	{
		MibInfo.printHeader(mibName, rootName, "", parentName, oid, ps);
	}

	private MibGenerator()
	{

	}
}
