package com.sppad.construction;

import java.util.LinkedList;

import org.snmp4j.smi.OID;

import com.google.common.base.Joiner;
import com.sppad.datastructures.primative.IntStack;
import com.sppad.snmp.JotsOID;

public class MibGenerator implements INodeVisitor
{
  private static String firstCharToUppercase(final String string) {
    
    final StringBuilder builder =  new StringBuilder(string);
    builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
    
    return builder.toString();
  }
  private final IntStack extStack = new IntStack();
  private final LinkedList<String> nameStack = new LinkedList<String>();
  private final IntStack oidStack = new IntStack();
  
  private final int[] prefix;

  public MibGenerator(final int[] prefix) {
    this.prefix = prefix;
  }
  
  public void printOid(final String nameString)
  {
    final OID oid = new JotsOID(prefix, oidStack, extStack);

    System.out.printf("%-20s %-20s\n", oid, nameString);
  }
  
  @Override
  public void visitEnter(final EntryNode node)
  {
    final String name = Joiner.on("").join(nameStack.toArray()) + "Entry";
    
    nameStack.addLast(firstCharToUppercase(node.name));
    oidStack.push(oidStack.pop() + 1);

    printOid(name);

    // setup for children
    oidStack.push(0);
  }

  @Override
  public void visitEnter(final LeafNode node)
  {
    nameStack.addLast(firstCharToUppercase(node.name));
    oidStack.push(oidStack.pop() + 1);

    if (!node.inTable)
      oidStack.push(0);

    final String name = Joiner.on("").join(nameStack.toArray());
    printOid(name);
  }

  @Override
  public void visitEnter(final RootNode node)
  {
    nameStack.addLast(firstCharToUppercase(node.name));
    oidStack.push(1);
    
    final String name = Joiner.on("").join(nameStack.toArray());
    printOid(name);

    // setup for children
    oidStack.push(0);
  }

  @Override
  public void visitEnter(final TableEntryNode node)
  {
    oidStack.push(oidStack.pop() + 1);

    final String name = Joiner.on("").join(nameStack.toArray()) + "Entry";
    printOid(name);

    // setup for children
    oidStack.push(0);
  }

  @Override
  public void visitEnter(final TableNode node)
  {
    nameStack.addLast(firstCharToUppercase(node.name));
    oidStack.push(oidStack.pop() + 1);

    final String name = Joiner.on("").join(nameStack.toArray()) + "Table";
    printOid(name);

    // setup for children
    oidStack.push(0);
  }

  @Override
  public void visitExit(final EntryNode node)
  {
    oidStack.pop();
    nameStack.removeLast();
  }

  @Override
  public void visitExit(final LeafNode node)
  {
    if (!node.inTable)
      oidStack.pop();

    nameStack.removeLast();
  }

  @Override
  public void visitExit(final RootNode node)
  {
    oidStack.pop();
    nameStack.removeLast();
  }

  @Override
  public void visitExit(final TableEntryNode node)
  {
    oidStack.pop();
  }

  @Override
  public void visitExit(final TableNode node)
  {
    oidStack.pop();
    nameStack.removeLast();
  }
}
