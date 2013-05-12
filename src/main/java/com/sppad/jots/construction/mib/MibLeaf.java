package com.sppad.jots.construction.mib;

import java.io.PrintStream;

public class MibLeaf {
	private static final int paddingSize = 20;

	static final String getSyntax(final Class<?> type) {
		if (type == Integer.class || type == Integer.TYPE)
			return "Integer32";

		if (type == Boolean.class || type == Boolean.TYPE)
			return "Boolean";

		if (type.isEnum())
			return type.getSimpleName();

		return "OCTET STRING (SIZE(0..65535))";
	}

	public static void addItem(final String name, final String parentName,
			final int oid, final Class<?> type, final String description,
			final boolean isWritable, final PrintStream ps) {
		final String typeName = getSyntax(type);
		final String maxAccess = isWritable ? "read-write" : "read-only";

		ps.println();
		ps.println(name + " OBJECT-TYPE");
		ps.println("\tSYNTAX\t\t" + typeName);
		ps.println("\tMAX-ACCESS\t" + maxAccess);
		ps.println("\tSTATUS\t\tcurrent");
		ps.println("\tDESCRIPTION");
		ps.println("\t\t\"" + description + "\"");

		ps.println("\t::= { " + parentName + " " + oid + " }");
	}
}