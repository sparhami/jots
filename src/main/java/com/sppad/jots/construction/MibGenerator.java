package com.sppad.jots.construction;

import java.util.LinkedList;
import java.util.Map;

import org.snmp4j.smi.OID;

import com.google.common.base.Joiner;
import com.sppad.jots.datastructures.primative.IntStack;
import com.sppad.jots.JotsOID;

public class MibGenerator
{
  private static class MibCreatingVisitor implements INodeVisitor
  {
    private static String firstCharToUppercase(final String string)
    {

      final StringBuilder builder = new StringBuilder(string);
      builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

      return builder.toString();
    }

    private final IntStack extStack = new IntStack();

    private final LinkedList<String> nameStack = new LinkedList<String>();

    private final int[] prefix;

    private final Map<Node, IntStack> staticOidMap;

    private MibCreatingVisitor(
        final int[] prefix,
        Map<Node, IntStack> staticOidMap)
    {
      this.staticOidMap = staticOidMap;
      this.prefix = prefix;
    }

    private void printOid(final String nameString, IntStack oidStack)
    {
      final OID oid = new JotsOID(prefix, oidStack);

      System.out.printf("%-20s %-20s\n", oid, nameString);
    }

    private void printTableOid(final String nameString, IntStack oidStack)
    {
      final OID oid = new JotsOID(prefix, oidStack, extStack);

      System.out.printf("%-20s %-20s\n", oid, nameString);
    }

    @Override
    public void visitEnter(final EntryNode node)
    {
      nameStack.addLast(firstCharToUppercase(node.name));

      final String name = Joiner.on("").join(nameStack.toArray()) + "Entry";
      printTableOid(name, staticOidMap.get(node));
    }

    @Override
    public void visitEnter(final LeafNode node)
    {
      nameStack.addLast(firstCharToUppercase(node.name));

      final String name = Joiner.on("").join(nameStack.toArray());

      if (node.inTable)
        printTableOid(name, staticOidMap.get(node));
      else
        printOid(name, staticOidMap.get(node));
    }

    @Override
    public void visitEnter(final RootNode node)
    {
      nameStack.addLast(firstCharToUppercase(node.name));

      final String name = Joiner.on("").join(nameStack.toArray());
      printTableOid(name, staticOidMap.get(node));
    }

    @Override
    public void visitEnter(final TableEntryNode node)
    {
      final String name = Joiner.on("").join(nameStack.toArray()) + "Entry";
      printTableOid(name, staticOidMap.get(node));
    }

    @Override
    public void visitEnter(final TableNode node)
    {
      nameStack.addLast(firstCharToUppercase(node.name));

      final String name = Joiner.on("").join(nameStack.toArray()) + "Table";
      printTableOid(name, staticOidMap.get(node));
    }

    @Override
    public void visitExit(final EntryNode node)
    {
      nameStack.removeLast();
    }

    @Override
    public void visitExit(final LeafNode node)
    {
      nameStack.removeLast();
    }

    @Override
    public void visitExit(final RootNode node)
    {
      nameStack.removeLast();
    }

    @Override
    public void visitExit(final TableEntryNode node)
    {

    }

    @Override
    public void visitExit(final TableNode node)
    {
      nameStack.removeLast();
    }
  }

  public static void createMib(
      final int[] prefix,
      Node node,
      Map<Node, IntStack> staticOidMap)
  {
    node.accept(new MibCreatingVisitor(prefix, staticOidMap));
  }
}
