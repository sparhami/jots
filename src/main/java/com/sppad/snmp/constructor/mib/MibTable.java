package com.sppad.snmp.constructor.mib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class MibTable extends MibSubtree
{
  public MibTable(final String parentName, final String name, final int oid,
      final boolean isParentTable, final String description,
      final List<String> indexTypes)
  {
    super(indexTypes);
    final String mibParentName = parentName
        + (isParentTable ? "Table" : "Entry");

    entryPrintStream.println();
    entryPrintStream.println(name + "Table  OBJECT-TYPE");
    entryPrintStream.println("\tSYNTAX\t\tSEQUENCE OF " + name + "Entry");
    entryPrintStream.println("\tMAX-ACCESS\tnot-accessible");
    entryPrintStream.println("\tSTATUS\t\tcurrent");
    entryPrintStream.println("\tDESCRIPTION");
    entryPrintStream.println("\t\t\"" + description + "\"");

    entryPrintStream.println("\t::= { " + mibParentName + " " + oid + " }");

    entryPrintStream.println();
    entryPrintStream.println(name + "Entry  OBJECT-TYPE");
    entryPrintStream.println("\tSYNTAX\t\t" + name + "EntryObj");
    entryPrintStream.println("\tMAX-ACCESS\tnot-accessible");
    entryPrintStream.println("\tSTATUS\t\tcurrent");

    // If this is a table and there are indicies, list them
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

  @Override
  public ByteArrayOutputStream finish() throws IOException
  {
    // Close the bracket from the SEQUENCE
    entryPrintStream.println("}");
    return super.finish();
  }
}