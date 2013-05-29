package com.sppad.jots.construction.mib;

import java.io.PrintStream;

import com.sppad.jots.construction.nodes.TableNode;
import com.sppad.jots.util.Strings;

public class MibTable
{
	public static void printTable(final TableNode node, final String childName,
			final String name, final String parentName, final int oid,
			final PrintStream ps)
	{

		final String sequenceName = Strings.firstCharToUppercase(childName);

		ps.println();
		ps.println(name + " OBJECT-TYPE");
		ps.println("\tSYNTAX\t\tSEQUENCE OF " + sequenceName);
		ps.println("\tMAX-ACCESS\tnot-accessible");
		ps.println("\tSTATUS\t\tcurrent");
		ps.println("\tDESCRIPTION");
		ps.println("\t\t\"" + "" + "\"");

		ps.println("\t::= { " + parentName + " " + oid + " }");
	}
}