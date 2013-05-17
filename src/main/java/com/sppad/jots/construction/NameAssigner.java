package com.sppad.jots.construction;

import java.util.LinkedList;

import com.google.common.base.Joiner;
import com.sppad.jots.construction.nodes.EntryNode;
import com.sppad.jots.construction.nodes.INodeVisitor;
import com.sppad.jots.construction.nodes.LeafNode;
import com.sppad.jots.construction.nodes.Node;
import com.sppad.jots.construction.nodes.RootNode;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;

public class NameAssigner {
	private static class NameAssigningVisitor implements INodeVisitor {

		private final Joiner joiner = Joiner.on("");

		private final LinkedList<String> nameStack = new LinkedList<String>();

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
			visitEnterNode(node);
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
			nameStack.addLast(node.name);

			final String name = joiner.join(nameStack) + node.getEnding();
			node.setProperty("NAME", name);
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
			visitExitNode();
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
			nameStack.removeLast();
		}
	}

	static void tag(final RootNode node) {
		final NameAssigningVisitor gen = new NameAssigningVisitor();
		node.accept(gen);
	}

	private NameAssigner() {

	}
}
