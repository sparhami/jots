package com.sppad.jots.construction;

import java.util.Deque;
import java.util.LinkedList;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sppad.jots.construction.nodes.EntryNode;
import com.sppad.jots.construction.nodes.INodeVisitor;
import com.sppad.jots.construction.nodes.LeafNode;
import com.sppad.jots.construction.nodes.Node;
import com.sppad.jots.construction.nodes.RootNode;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;
import com.sppad.jots.util.Strings;

class NameAssigner {
	private static class NameAssigningVisitor implements INodeVisitor {

		private final Deque<String> nameStack = Lists.newLinkedList();
		
		@Override
		public void visitEnter(final EntryNode node) {
			visitEnterNode(node);
		}

		@Override
		public void visitEnter(final LeafNode node) {
			visitEnterNode(node);
		}

		@Override
		public void visitEnter(final RootNode node) {
			node.setProperty("NAME", node.name);
			nameStack.push(node.name);
		}

		@Override
		public void visitEnter(final TableEntryNode node) {
			visitEnterNode(node);
		}

		@Override
		public void visitEnter(final TableNode node) {
			visitEnterNode(node);
		}

		private void visitEnterNode(final Node node) {
			final String name = nameStack.peek() + Strings.firstCharToUppercase(node.name);
			
			node.setProperty("NAME", name + node.getEnding());
			nameStack.push(name);
		}

		@Override
		public void visitExit(final EntryNode node) {
			visitExitNode();
		}

		@Override
		public void visitExit(final LeafNode node) {
			visitExitNode();
		}

		@Override
		public void visitExit(final RootNode node) {
			nameStack.pop();
		}

		@Override
		public void visitExit(final TableEntryNode node) {
			visitExitNode();
		}

		@Override
		public void visitExit(final TableNode node) {
			visitExitNode();
		}

		private void visitExitNode() {
			nameStack.pop();
		}
	}

	static void tag(final RootNode node) {
		final NameAssigningVisitor gen = new NameAssigningVisitor();
		
		node.accept(gen);
	}

	private NameAssigner() {

	}
}
