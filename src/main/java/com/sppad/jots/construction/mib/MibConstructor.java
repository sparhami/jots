package com.sppad.jots.construction.mib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MibConstructor
{
	private final ByteArrayOutputStream createMibStream = new ByteArrayOutputStream();

	private final Map<String, MibSubtree> entryMap = new LinkedHashMap<String, MibSubtree>();

	private final Set<Class<? extends Enum<?>>> enumSyntaxSet = new HashSet<Class<? extends Enum<?>>>();

	private final String mibName;

	private final int mibTree;

	private final OutputStream outstream;

	private final String parentTree;

	private final PrintStream ps;

	private final String rootName;

	public MibConstructor(final String mibName, final String rootName,
			final String parentTree, final int mibTree, final OutputStream os)
	{
		this.mibName = mibName;
		this.rootName = rootName;
		this.parentTree = parentTree;
		this.mibTree = mibTree;

		outstream = os;
		ps = new PrintStream(createMibStream);

		MibSubtree entry = new MibRoot();
		entryMap.put(rootName, entry);
	}

	public void addEntry(String parentName, final String name, final int oid,
							final String description)
	{
		if (parentName == null || parentName.equals(""))
			parentName = rootName;

		final MibEntry entry = new MibEntry();
		entryMap.put(name, entry);

		entry.addEntry(parentName, name, oid, description);
	}

	@SuppressWarnings("unchecked")
	public void addItem(String parentName, final String name, final int oid,
						final Class<?> type, final String description,
						final boolean isWritable)
	{
		if (type.isEnum())
			addEnum((Class<? extends Enum<?>>) type);

		if (parentName == null || parentName.equals(""))
			parentName = rootName;

		final MibSubtree entry = entryMap.get(parentName);
		entry.addItem(parentName, name, oid, type, description, isWritable);
	}

	public void addTable(String parentName, final String name, final int oid,
							final boolean isParentTable,
							final String description, final Class<?> keyType)
	{
		if (parentName == null || parentName.equals(""))
			parentName = rootName;

		final MibSubtree parentEntry = entryMap.get(parentName);
		final List<String> indexTypes = new ArrayList<String>(
				parentEntry.indexTypes);

		final String indexType = (keyType == String.class) ? "IndexString" : "IndexInteger";
		indexTypes.add(indexType);

		final MibSubtree entry = new MibTable(parentName, name, oid,
				isParentTable, description, indexTypes);
		entryMap.put(name, entry);
	}

	public void finish() throws IOException
	{
		ps.print(MibInfo.createMibHeader(mibName, rootName + "Entry", "",
				parentTree, mibTree));

		for (final MibSubtree entry : entryMap.values())
			entry.finish().writeTo(ps);

		ps.println("\nEND");

		createMibStream.writeTo(outstream);
		outstream.close();
	}

	private void addEnum(final Class<? extends Enum<?>> enumClass)
	{
		if (enumSyntaxSet.contains(enumClass))
			return;

		enumSyntaxSet.add(enumClass);

		final StringBuilder builder = new StringBuilder();

		builder.append(enumClass.getSimpleName() + " ::= TEXTUAL-CONVENTION\n");
		builder.append("\tSYNTAX      OCTET STRING {");
		for (final Enum<?> enumElement : enumClass.getEnumConstants())
			builder.append(" \"" + enumElement.name() + "\",");

		builder.deleteCharAt(builder.lastIndexOf(","));
		builder.append(" }\n");

		ps.println(builder.toString());
	}
}
