package com.sppad.jots.construction;

import org.snmp4j.smi.OID;

import com.sppad.jots.datastructures.primative.IntStack;
import com.sppad.jots.JotsOID;

public class TreeConstructor implements INodeVisitor
{
  private final IntStack extStack = new IntStack();
  private final IntStack oidStack = new IntStack();
  
  private final int[] prefix;

  public TreeConstructor(final int[] prefix) {
    this.prefix = prefix;
  }
  
  private void printOid()
  {
    final OID oid = new JotsOID(prefix, oidStack, extStack);

    System.out.println(oid);
  }
  
  @Override
  public void visitEnter(final EntryNode node)
  {
    oidStack.push(oidStack.pop() + 1);
    oidStack.push(0);
  }

  @Override
  public void visitEnter(final LeafNode node)
  {
    oidStack.push(oidStack.pop() + 1);

    if (!node.inTable)
      oidStack.push(0);
  }

  @Override
  public void visitEnter(final RootNode node)
  {
    oidStack.push(1);
    oidStack.push(0);
  }

  @Override
  public void visitEnter(final TableEntryNode node)
  {
    oidStack.push(oidStack.pop() + 1);
    oidStack.push(0);
  }

  @Override
  public void visitEnter(final TableNode node)
  {
    oidStack.push(oidStack.pop() + 1);
    oidStack.push(0);
  }

  @Override
  public void visitExit(final EntryNode node)
  {
    oidStack.pop();
  }

  @Override
  public void visitExit(final LeafNode node)
  {
    if (!node.inTable)
      oidStack.pop();
  }

  @Override
  public void visitExit(final RootNode node)
  {
    oidStack.pop();
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
  }
}
