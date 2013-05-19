package com.sppad.jots.construction.mib;

import java.io.PrintStream;
import java.util.Collection;
import com.google.common.base.Joiner;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;
import com.sppad.jots.util.Strings;

public class MibTable {
	public static void printTable(final TableNode node, final String childName,
			final String name, final String parentName, final int oid,
			final PrintStream ps) {

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

	public static void printEntryStart(final TableEntryNode node,
			final String name, final String parentName,
			final Collection<String> indicies, final PrintStream ps) {
		final String sequenceName = Strings.firstCharToUppercase(name);
		final String indiciesText = Joiner.on(", ").join(indicies);

		ps.println();
		ps.println(name + " OBJECT-TYPE");
		ps.println("\tSYNTAX\t\t" + sequenceName);
		ps.println("\tMAX-ACCESS\tnot-accessible");
		ps.println("\tSTATUS\t\tcurrent");

		ps.print("\tINDEX\t\t{ ");
		ps.print(indiciesText);
		ps.print(" }\n");

		ps.println("\t::= { " + parentName + " 1 }");

		ps.println();
		ps.println(sequenceName + " ::= SEQUENCE {");
	}

	public static void printEntrySequence(final String name, final String type,
			final PrintStream ps) {
		ps.println("\t" + name + "\t" + type);
	}

	public static void printEntryEnd(final PrintStream ps) {
		ps.println("}\n\n");
	}
}