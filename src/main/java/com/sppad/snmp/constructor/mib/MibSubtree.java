package com.sppad.snmp.constructor.mib;

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

    public MibSubtree(List<String> indexTypes)
    {
        this.indexTypes = indexTypes;
    }
    
    public MibSubtree()
    {
        this.indexTypes = Collections.emptyList();
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

    private static final String getSyntax(Class<?> type)
    {
        if (type == Integer.class || type == Integer.TYPE)
            return "Integer32";

        if (type == Boolean.class || type == Boolean.TYPE)
            return "Boolean";

        if (type.isEnum())
            return type.getSimpleName();

        return "OCTET STRING (SIZE(0..65535))";
    }

    public void addItem(String parentName, String name, int oid, Class<?> type,
            String description, boolean isWritable)
    {
        addSequenceEntry(name);

        String typeName = getSyntax(type);
        String maxAccess = isWritable ? "read-write" : "read-only";

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

    protected void addSequenceEntry(String name)
    {
        entryPrintStream.print(String
                .format("\t%1$-" + paddingSize + "s", name));
        entryPrintStream.println("\tOCTET STRING,");
    }

    public ByteArrayOutputStream finish() throws IOException
    {
        itemByteStream.writeTo(entryPrintStream);
        return entryByteStream;
    }
}