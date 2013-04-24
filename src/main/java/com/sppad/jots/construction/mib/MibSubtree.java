package com.sppad.jots.construction.mib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public class MibSubtree
{
	private static final int paddingSize = 20;

	protected final ByteArrayOutputStream entryByteStream = new ByteArrayOutputStream();

	protected final PrintStream entryPrintStream = new PrintStream(
			entryByteStream);

	protected final List<String> indexTypes;

	protected final ByteArrayOutputStream itemByteStream = new ByteArrayOutputStream();

	protected final PrintStream itemPrintStream = new PrintStream(
			itemByteStream);

	private static final String getSyntax(final Class<?> type)
	{
		if (type == Integer.class || type == Integer.TYPE)
			return "Integer32";

		if (type == Boolean.class || type == Boolean.TYPE)
			return "Boolean";

		if (type.isEnum())
			return type.getSimpleName();

		return "OCTET STRING (SIZE(0..65535))";
	}

	public MibSubtree()
	{
		this.indexTypes = Collections.emptyList();
	}

	public MibSubtree(final List<String> indexTypes)
	{
		this.indexTypes = indexTypes;
	}

	public void addEntry(final String parentName, final String name,
							final int oid, final String description)
	{
		entryPrintStream.println();
		entryPrintStream.println(name + "Entry  OBJECT-TYPE");
		entryPrintStream.println("\tSYNTAX\t\t" + name + "EntryObj");
		entryPrintStream.println("\tMAX-ACCESS\tnot-accessible");
		entryPrintStream.println("\tSTATUS\t\tcurrent");
		entryPrintStream.println("\t::= { " + parentName + "Entry " + oid
				+ " }");

		entryPrintStream.println();
		entryPrintStream.println(name + "EntryObj ::= SEQUENCE {");
	}

	public void addItem(final String parentName, final String name,
						final int oid, final Class<?> type,
						final String description, final boolean isWritable)
	{
		addSequenceEntry(name);

		final String typeName = getSyntax(type);
		final String maxAccess = isWritable ? "read-write" : "read-only";

		itemPrintStream.println();
		itemPrintStream.println(name + " OBJECT-TYPE");
		itemPrintStream.println("\tSYNTAX\t\t" + typeName);
		itemPrintStream.println("\tMAX-ACCESS\t" + maxAccess);
		itemPrintStream.println("\tSTATUS\t\tcurrent");
		itemPrintStream.println("\tDESCRIPTION");
		itemPrintStream.println("\t\t\"" + description + "\"");

		itemPrintStream
				.println("\t::= { " + parentName + "Entry " + oid + " }");
	}

	public ByteArrayOutputStream finish() throws IOException
	{
		itemByteStream.writeTo(entryPrintStream);
		return entryByteStream;
	}

	protected void addSequenceEntry(final String name)
	{
		entryPrintStream.print(String
				.format("\t%1$-" + paddingSize + "s", name));
		entryPrintStream.println("\tOCTET STRING,");
	}
}