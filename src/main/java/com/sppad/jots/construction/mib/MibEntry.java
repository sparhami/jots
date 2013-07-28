package com.sppad.jots.construction.mib;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.sppad.jots.construction.nodes.Node;
import com.sppad.jots.util.Strings;

public class MibEntry
{
	private static void printEntryEnd(final PrintStream ps)
	{
		ps.println("}\n");
	}

	private static void printEntrySequence(final String name,
			final String type, final PrintStream ps)
	{
		ps.println("\t" + name + "\t" + type);
	}

	private static void printEntryStart(final Node node, final String name,
			final String parentName, final int subtree,
			@Nullable final Collection<String> indicies, final PrintStream ps)
	{
		final String sequenceName = Strings.firstCharToUppercase(name);

		ps.println();
		ps.println(name + " OBJECT-TYPE");
		ps.println("\tSYNTAX\t\t" + sequenceName);
		ps.println("\tMAX-ACCESS\tnot-accessible");
		ps.println("\tSTATUS\t\tcurrent");

		printIndicies(indicies, ps);

		ps.println("\t::= { " + parentName + " " + subtree + " }");

		ps.println();
		ps.println(sequenceName + " ::= SEQUENCE {");
	}

	private static void printIndicies(
			@Nullable final Collection<String> indicies, final PrintStream ps)
	{
		if (indicies == null || indicies.size() == 0)
			return;

		final String indiciesText = Joiner.on(", ").join(indicies);

		ps.print("\tINDEX\t\t{ ");
		ps.print(indiciesText);
		ps.print(" }\n");
	}

	private final String name;

	private final Node node;

	private final String parentName;

	public MibEntry(final Node node)
	{
		this.node = node;
		this.name = (String) node.getProperty("NAME");
		this.parentName = (String) node.snmpParent.getProperty("NAME");
	}

	public void write(PrintStream ps)
	{

		final int[] staticOid = (int[]) node.getProperty("OID");
		final int subtree = staticOid[staticOid.length - 1];

		printEntryStart(node, name, parentName, subtree,
				new LinkedList<String>(), ps);

		for (final Node child : node.snmpNodes)
		{
			final String childName = (String) child.getProperty("NAME");
			printEntrySequence(childName, "", ps);
		}

		printEntryEnd(ps);

	}
}
