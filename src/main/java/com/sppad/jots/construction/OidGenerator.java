package com.sppad.jots.construction;

import java.util.Map;

import com.google.common.collect.Maps;
import com.sppad.jots.datastructures.primative.IntStack;

public class OidGenerator
{
	private static class OidAssigningVisitor implements INodeVisitor
	{
		private final Map<Node, int[]> nodeToStaticOidMap = Maps.newHashMap();

		private final IntStack oidStack = new IntStack();

		private OidAssigningVisitor()
		{
			this.oidStack.push(0);
		}

		@Override
		public void visitEnter(final EntryNode node)
		{
			visitEnterNode(node);
		}

		@Override
		public void visitEnter(final LeafNode node)
		{
			visitEnterNode(node);
		}

		@Override
		public void visitEnter(final RootNode node)
		{
			visitEnterNode(node);
		}

		@Override
		public void visitEnter(final TableEntryNode node)
		{
			visitEnterNode(node);
		}

		@Override
		public void visitEnter(final TableNode node)
		{
			visitEnterNode(node);
		}

		private void visitEnterNode(final Node node)
		{
			oidStack.push(oidStack.pop() + 1);

			nodeToStaticOidMap.put(node, oidStack.toArray());

			oidStack.push(0);
		}

		@Override
		public void visitExit(final EntryNode node)
		{
			visitExitNode(node);
		}

		@Override
		public void visitExit(final LeafNode node)
		{
			visitExitNode(node);
		}

		@Override
		public void visitExit(final RootNode node)
		{
			visitExitNode(node);
		}

		@Override
		public void visitExit(final TableEntryNode node)
		{
			visitExitNode(node);
		}

		@Override
		public void visitExit(final TableNode node)
		{
			visitExitNode(node);
		}

		private void visitExitNode(final Node node)
		{
			oidStack.pop();
		}
	}

	public static Map<Node, int[]> getStaticOidParts(final Node node)
	{
		final OidAssigningVisitor visitor = new OidAssigningVisitor();
		node.accept(visitor);

		return visitor.nodeToStaticOidMap;
	}

	private OidGenerator()
	{

	}
}
