package com.sppad.construction;

interface INodeVisitor
{
  void visitEnter(EntryNode node);

  void visitEnter(LeafNode node);

  void visitEnter(RootNode node);

  void visitEnter(TableEntryNode node);

  void visitEnter(TableNode node);

  void visitExit(EntryNode node);

  void visitExit(LeafNode node);

  void visitExit(RootNode node);

  void visitExit(TableEntryNode node);

  void visitExit(TableNode node);
}
