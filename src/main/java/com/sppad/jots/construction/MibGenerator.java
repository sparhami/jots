package com.sppad.jots.construction;

import java.io.OutputStream;
import java.io.PrintStream;

import org.snmp4j.smi.OID;

import com.sppad.jots.JotsOID;
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

class MibGenerator implements INodeVisitor
{
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

		final MibGenerator gen = new MibGenerator(prefix, ps);
		node.accept(gen);

		printFooter(ps);
	}

	static void printFooter(final PrintStream ps)
	{
		ps.print("END");
	}

	static void printHeader(final String mibName, final String rootName,
			final String parentName, int oid, final PrintStream ps)
	{
		ps.print(MibInfo
				.createMibHeader(mibName, rootName, "", parentName, oid));
	}

	private final int[] prefix;

	private final PrintStream ps;

	private MibGenerator(final int[] prefix, final PrintStream ps)
	{
		this.prefix = prefix;
		this.ps = ps;
	}

	@Override
	public void visitEnter(final EntryNode node)
	{
		final String name = (String) node.getProperty("NAME");
		final int[] staticOid = (int[]) node.getProperty("OID");

		MibEntry entry = new MibEntry(node);
		entry.write(ps);

		printOid(name, JotsOID.createOID(prefix, staticOid));
	}

	@Override
	public void visitEnter(final LeafNode node)
	{
		final String parentName = (String) node.snmpParent.getProperty("NAME");
		final String name = (String) node.getProperty("NAME");
		final int[] staticOid = (int[]) node.getProperty("OID");

		MibLeaf.addItem(name, parentName, staticOid[staticOid.length - 1],
				node.klass, "", true, ps);

		printOid(name, JotsOID.createOID(prefix, staticOid));
	}

	@Override
	public void visitEnter(final RootNode node)
	{
		final String name = (String) node.getProperty("NAME");
		final int[] staticOid = (int[]) node.getProperty("OID");

		printOid(name, JotsOID.createOID(prefix, staticOid));
	}

	@Override
	public void visitEnter(final TableEntryNode node)
	{
		final String name = (String) node.getProperty("NAME");
		final int[] staticOid = (int[]) node.getProperty("OID");

		MibEntry entry = new MibEntry(node);
		entry.write(ps);

		printOid(name, JotsOID.createOID(prefix, staticOid));
	}

	@Override
	public void visitEnter(final TableNode node)
	{
		final String parentName = (String) node.snmpParent.getProperty("NAME");
		final String name = (String) node.getProperty("NAME");
		final String childName = (String) node.getEntry().getProperty("NAME");
		final int[] staticOid = (int[]) node.getProperty("OID");

		MibTable.printTable(node, childName, name, parentName,
				staticOid[staticOid.length - 1], ps);

		printOid(name, JotsOID.createOID(prefix, staticOid));
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

	private void printOid(final String nameString, OID oid)
	{
		System.out.printf("%-20s %-20s\n", oid, nameString);
	}
}
