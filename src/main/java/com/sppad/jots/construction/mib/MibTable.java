package com.sppad.jots.construction.mib;

import java.io.PrintStream;
import java.util.Collection;
import com.google.common.base.Joiner;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;

public class MibTable {
	public static void printTable(final TableNode node, final String name,
			final String parentName, final int oid, final PrintStream ps) {
		ps.println();
		ps.println(name + " OBJECT-TYPE");
		ps.println("\tSYNTAX\t\tSEQUENCE OF " + name);
		ps.println("\tMAX-ACCESS\tnot-accessible");
		ps.println("\tSTATUS\t\tcurrent");
		ps.println("\tDESCRIPTION");
		ps.println("\t\t\"" + "" + "\"");

		ps.println("\t::= { " + parentName + " " + oid + " }");
	}

	public static void printEntryStart(final TableEntryNode node,
			final String name, final String parentName,
			final Collection<String> indicies, final PrintStream ps) {
		final String indiciesText = Joiner.on(", ").join(indicies);

		ps.println();
		ps.println(name + " OBJECT-TYPE");
		ps.println("\tSYNTAX\t\t" + name);
		ps.println("\tMAX-ACCESS\tnot-accessible");
		ps.println("\tSTATUS\t\tcurrent");

		ps.print("\tINDEX\t\t{ ");
		ps.print(indiciesText);
		ps.print(" }\n");

		ps.println("\t::= { " + name + " 1 }");

		ps.println();
		ps.println(name + " ::= SEQUENCE {");
	}

	public static void printEntrySequence(final String name, final String type,
			final PrintStream ps) {
		ps.println("\t" + name + "\t" + type);
	}

	public static void printEntryEnd(final PrintStream ps) {
		ps.println("}\n\n");
	}
}