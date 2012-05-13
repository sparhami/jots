package com.sppad.snmp.constructor.mib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private class MibEntry
    {
	private ByteArrayOutputStream entryByteStream = new ByteArrayOutputStream();

	private PrintStream entryPrintStream = new PrintStream(entryByteStream);

	private ByteArrayOutputStream itemByteStream = new ByteArrayOutputStream();

	private PrintStream itemPrintStream = new PrintStream(itemByteStream);

	private boolean isRoot = false;

	private List<String> indexTypes;

	public MibEntry()
	{

	}

	public MibEntry(List<String> indexTypes)
	{
	    this.indexTypes = indexTypes;

	}

	public MibEntry(boolean isRoot)
	{
	    this.isRoot = isRoot;
	}

	public void addItem(String parentName, String name, int oid,
		Class<?> type, String description, boolean isWritable)
	{

	    String typeName;

	    if (type == Integer.class || type == Integer.TYPE)
		typeName = "Integer32";
	    else if (type == Boolean.class || type == Boolean.TYPE)
		typeName = "Boolean";
	    else if (type.isEnum())
		typeName = type.getSimpleName();
	    else
		typeName = "OCTET STRING (SIZE(0..65535))";
	    
	    String maxAccess = isWritable ? "read-write" : "read-only";

	    if (!isRoot)
	    {
		entryPrintStream.print(String.format("\t%1$-" + paddingSize
			+ "s", name));
		entryPrintStream.println("\tOCTET STRING,");
	    }

	    itemPrintStream.println();
	    itemPrintStream.println(name + " OBJECT-TYPE");
	    itemPrintStream.println("\tSYNTAX\t\t" + typeName);
	    itemPrintStream.println("\tMAX-ACCESS\t" + maxAccess);
	    itemPrintStream.println("\tSTATUS\t\tcurrent");
	    itemPrintStream.println("\tDESCRIPTION");
	    itemPrintStream.println("\t\t\"" + description + "\"");

	    itemPrintStream.println("\t::= { " + parentName + "Entry " + oid
		    + " }");
	}

	public void addEntry(String parentName, String name, int oid,
		String description)
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

	public void addTable(String parentName, String name, int oid,
		boolean isParentTable, String description,
		List<String> indexTypes)
	{
	    parentName += isParentTable ? "Table" : "Entry";

	    entryPrintStream.println();
	    entryPrintStream.println(name + "Table  OBJECT-TYPE");
	    entryPrintStream.println("\tSYNTAX\t\tSEQUENCE OF " + name
		    + "EntryObj");
	    entryPrintStream.println("\tMAX-ACCESS\tnot-accessible");
	    entryPrintStream.println("\tSTATUS\t\tcurrent");
	    entryPrintStream.println("\tDESCRIPTION");
	    entryPrintStream.println("\t\t\"" + description + "\"");

	    entryPrintStream
		    .println("\t::= { " + parentName + " " + oid + " }");

	    entryPrintStream.println();
	    entryPrintStream.println(name + "Entry  OBJECT-TYPE");
	    entryPrintStream.println("\tSYNTAX\t\t" + name + "EntryObj");
	    entryPrintStream.println("\tMAX-ACCESS\tnot-accessible");
	    entryPrintStream.println("\tSTATUS\t\tcurrent");
	    if (indexTypes != null && indexTypes.size() > 0)
	    {
		entryPrintStream.print("\tINDEX\t\t{ ");
		for (int i = 0; i < indexTypes.size(); i++)
		{
		    entryPrintStream.print(indexTypes.get(i));
		    if (i + 1 < indexTypes.size())
			entryPrintStream.print(", ");
		}

		entryPrintStream.println(" }");
	    }

	    entryPrintStream.println("\t::= { " + name + "Table 1 }");

	    entryPrintStream.println();
	    entryPrintStream.println(name + "EntryObj ::= SEQUENCE {");
	}

	public void finish() throws IOException
	{

	    if (!isRoot)
	    {
		entryPrintStream.println("}");
	    }

	    itemByteStream.writeTo(entryPrintStream);
	}
    }

    private final Set<Class<? extends Enum<?>>> enumSyntaxSet = new HashSet<Class<? extends Enum<?>>>();

    private final PrintStream ps;

    private Map<String, MibEntry> entryMap = new LinkedHashMap<String, MibEntry>();

    private int paddingSize = 20;

    private String rootName;

    private OutputStream outstream;

    private ByteArrayOutputStream createMibStream = new ByteArrayOutputStream();

    public MibConstructor(String rootName, OutputStream os)
    {
	this.rootName = rootName;

	outstream = os;
	ps = new PrintStream(createMibStream);

	MibEntry entry = new MibEntry(true);
	entryMap.put(rootName, entry);
    }

    private void printMibHeader() throws IOException
    {
	InputStream is = this.getClass().getResourceAsStream("/mibPreamble.txt");

	byte[] readbuf = new byte[256];
	while (is.available() > 0)
	{
	    int len = is.read(readbuf);
	    ps.write(readbuf, 0, len);
	}

	ps.println();
	ps.println();
	ps.println(rootName + "Entry MODULE-IDENTITY");
	ps.println("\tLAST-UPDATED \"200005110000Z\"  -- 11 May, 2000");
	ps.println("\tORGANIZATION \"Test\"");
	ps.println("\tCONTACT-INFO");
	ps.println("\t\t\"Test Person");
	ps.println("\t\tPhone: +1-650-948-6500");
	ps.println("\t\tFax:   +1-650-745-0671");
	ps.println("\t\tEmail: test@test.com\"");
	ps.println("\tDESCRIPTION");
	ps.println("\t\t\"This is a test MIB\"");
	ps.println();
	ps.println("::= { enterprises 15001 }");
    }

    private void addEnum(Class<? extends Enum<?>> enumClass)
    {
	if (enumSyntaxSet.contains(enumClass))
	    return;

	enumSyntaxSet.add(enumClass);

	StringBuilder builder = new StringBuilder();

	builder.append(enumClass.getSimpleName() + " ::= TEXTUAL-CONVENTION\n");
	builder.append("\tSYNTAX      OCTET STRING {");
	for (Enum<?> enumElement : enumClass.getEnumConstants())
	    builder.append(" \"" + enumElement.name() + "\",");

	builder.deleteCharAt(builder.lastIndexOf(","));
	builder.append(" }\n");

	ps.println(builder.toString());
    }

    @SuppressWarnings("unchecked")
    public void addItem(String parentName, String name, int oid, Class<?> type,
	    String description, boolean isWritable)
    {
	if (type.isEnum())
	    addEnum((Class<? extends Enum<?>>) type);

	if (parentName == null || parentName.equals(""))
	    parentName = rootName;

	MibEntry entry = entryMap.get(parentName);
	entry.addItem(parentName, name, oid, type, description, isWritable);
    }

    public void addTable(String parentName, String name, int oid,
	    boolean isParentTable, String description, Class<?> keyType)
    {
	if (parentName == null || parentName.equals(""))
	    parentName = rootName;

	MibEntry parentEntry = entryMap.get(parentName);
	List<String> indexTypes;
	if (parentEntry != null && parentEntry.indexTypes != null)
	{
	    indexTypes = new ArrayList<String>();
	    for (String indexType : parentEntry.indexTypes)
		indexTypes.add(indexType);
	}
	else
	{
	    indexTypes = new ArrayList<String>();
	}

	String indexType = "IndexInteger";
	if (keyType == String.class)
	    indexType = "IndexString";

	indexTypes.add(indexType);

	MibEntry entry = new MibEntry(indexTypes);
	entryMap.put(name, entry);

	entry.addTable(parentName, name, oid, isParentTable, description,
		indexTypes);
    }

    public void addEntry(String parentName, String name, int oid,
	    String description)
    {
	if (parentName == null || parentName.equals(""))
	    parentName = rootName;

	MibEntry entry = new MibEntry();
	entryMap.put(name, entry);

	entry.addEntry(parentName, name, oid, description);
    }

    public void finish() throws IOException
    {

	printMibHeader();
	
	for (MibEntry entry : entryMap.values())
	{
	    entry.finish();
	    entry.entryByteStream.writeTo(ps);
	}

	ps.println();
	ps.println("END");

	createMibStream.writeTo(outstream);
	outstream.close();
    }
}
