package com.sppad.jots.construction.mib;

import java.io.PrintStream;
import java.util.Set;

import com.google.common.collect.Sets;
import com.sppad.jots.construction.nodes.EntryNode;
import com.sppad.jots.construction.nodes.INodeVisitor;
import com.sppad.jots.construction.nodes.LeafNode;
import com.sppad.jots.construction.nodes.RootNode;
import com.sppad.jots.construction.nodes.TableEntryNode;
import com.sppad.jots.construction.nodes.TableNode;

public class TextualConvention
{
	private static class TextualConventionVisitor implements INodeVisitor
	{
		private final Set<Class<?>> visitedEnums = Sets.newHashSet();

		private final PrintStream ps;

		TextualConventionVisitor(PrintStream ps)
		{
			this.ps = ps;
		}

		@Override
		public void visitEnter(EntryNode node)
		{

		}

		@SuppressWarnings("unchecked")
		@Override
		public void visitEnter(LeafNode node)
		{
			if (!node.klass.isEnum() || !visitedEnums.add(node.klass))
				return;

			ps.append(createTextualConvention((Class<? extends Enum<?>>) node.klass));
		}

		@Override
		public void visitEnter(RootNode node)
		{

		}

		@Override
		public void visitEnter(TableEntryNode node)
		{

		}

		@Override
		public void visitEnter(TableNode node)
		{

		}

		@Override
		public void visitExit(EntryNode node)
		{

		}

		@Override
		public void visitExit(LeafNode node)
		{

		}

		@Override
		public void visitExit(RootNode node)
		{

		}

		@Override
		public void visitExit(TableEntryNode node)
		{

		}

		@Override
		public void visitExit(TableNode node)
		{

		}

	}

	public static void addTextualConventions(final RootNode node,
			final PrintStream ps)
	{
		addBooleanTextualConvention(ps);
		
		node.accept(new TextualConventionVisitor(ps));
	}
	
	private static void addBooleanTextualConvention(final PrintStream ps) {
		
		ps.append("Boolean ::= TEXTUAL-CONVENTION\n");
		ps.append("\tSYNTAX		OCTET STRING { \"true\", \"false\" }\n\n");
	}
	
	static String createTextualConvention(
			final Class<? extends Enum<?>> cls)
	{
		final StringBuilder builder = new StringBuilder();

		builder.append(cls.getSimpleName() + " ::= TEXTUAL-CONVENTION\n");
		builder.append("\tSYNTAX      OCTET STRING {");

		for (final Enum<?> enumElement : cls.getEnumConstants())
			builder.append(" \"" + enumElement.name() + "\",");

		builder.deleteCharAt(builder.lastIndexOf(","));
		builder.append(" }\n\n");

		return builder.toString();
	}
	
	private TextualConvention() {
		
	}
}
