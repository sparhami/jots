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
  private ByteArrayOutputStream createMibStream = new ByteArrayOutputStream();

  private Map<String, MibSubtree> entryMap = new LinkedHashMap<String, MibSubtree>();

  private final Set<Class<? extends Enum<?>>> enumSyntaxSet = new HashSet<Class<? extends Enum<?>>>();

  private OutputStream outstream;

  private final PrintStream ps;

  private final String mibName;

  private final String rootName;

  private final String parentTree;

  private final int mibTree;

  public MibConstructor(String mibName, String rootName, String parentTree,
      int mibTree, OutputStream os)
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

  public void addEntry(String parentName, String name, int oid,
      String description)
  {
    if (parentName == null || parentName.equals(""))
      parentName = rootName;

    MibEntry entry = new MibEntry();
    entryMap.put(name, entry);

    entry.addEntry(parentName, name, oid, description);
  }

  @SuppressWarnings("unchecked")
  public void addItem(String parentName, String name, int oid, Class<?> type,
      String description, boolean isWritable)
  {
    if (type.isEnum())
      addEnum((Class<? extends Enum<?>>) type);

    if (parentName == null || parentName.equals(""))
      parentName = rootName;

    MibSubtree entry = entryMap.get(parentName);
    entry.addItem(parentName, name, oid, type, description, isWritable);
  }

  public void addTable(String parentName, String name, int oid,
      boolean isParentTable, String description, Class<?> keyType)
  {
    if (parentName == null || parentName.equals(""))
      parentName = rootName;

    MibSubtree parentEntry = entryMap.get(parentName);
    List<String> indexTypes = new ArrayList<String>(parentEntry.indexTypes);

    String indexType = (keyType == String.class) ? "IndexString"
        : "IndexInteger";
    indexTypes.add(indexType);

    MibSubtree entry = new MibTable(parentName, name, oid, isParentTable,
        description, indexTypes);
    entryMap.put(name, entry);
  }

  public void finish() throws IOException
  {
    ps.print(MibInfo.createMibHeader(mibName, rootName + "Entry", "",
        parentTree, mibTree));

    for (MibSubtree entry : entryMap.values())
      entry.finish().writeTo(ps);

    ps.println("\nEND");

    createMibStream.writeTo(outstream);
    outstream.close();
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
}
