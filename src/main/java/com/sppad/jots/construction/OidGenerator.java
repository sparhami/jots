package com.sppad.jots.construction;

import java.util.Map;

import com.google.common.collect.Maps;
import com.sppad.jots.datastructures.primative.IntStack;

public class OidGenerator
{
	private static class OidAssigningVisitor implements INodeVisitor
	{
		private final Map<Node, IntStack> nodeToStaticOidMap = Maps
				.newHashMap();

		private final IntStack oidStack = new IntStack();

		private OidAssigningVisitor()
		{
			this.oidStack.push(0);
		}

		@Override
		public void visitEnter(final EntryNode node)
		{
			oidStack.push(oidStack.pop() + 1);

			nodeToStaticOidMap.put(node, new IntStack(oidStack));

			oidStack.push(0);
		}

		@Override
		public void visitEnter(final LeafNode node)
		{
			oidStack.push(oidStack.pop() + 1);

			nodeToStaticOidMap.put(node, new IntStack(oidStack));
		}

		@Override
		public void visitEnter(final RootNode node)
		{
			oidStack.push(oidStack.pop() + 1);

			nodeToStaticOidMap.put(node, new IntStack(oidStack));

			oidStack.push(0);
		}

		@Override
		public void visitEnter(final TableEntryNode node)
		{
			oidStack.push(oidStack.pop() + 1);

			nodeToStaticOidMap.put(node, new IntStack(oidStack));

			oidStack.push(0);
		}

		@Override
		public void visitEnter(final TableNode node)
		{
			oidStack.push(oidStack.pop() + 1);

			nodeToStaticOidMap.put(node, new IntStack(oidStack));

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

	public static Map<Node, IntStack> getStaticOidParts(Node node)
	{
		OidAssigningVisitor visitor = new OidAssigningVisitor();
		node.accept(visitor);

		return visitor.nodeToStaticOidMap;
	}
}
