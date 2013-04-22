package com.sppad.jots.construction;

abstract class InnerNode extends Node
{
  InnerNode(
      final Class<?> klass,
      final Node parent,
      final boolean inTable,
      final String name)
  {
    super(klass, parent, inTable, name);
  }
}
