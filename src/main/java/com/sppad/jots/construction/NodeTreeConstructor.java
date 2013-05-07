package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.sppad.jots.annotations.Jots;
import com.sppad.jots.annotations.SnmpTableIndex;
import com.sppad.jots.log.ErrorMessage;
import com.sppad.jots.util.Fields;

class NodeTreeConstructor
{
	private static final Logger logger = LoggerFactory
			.getLogger(NodeTreeConstructor.class);

	static Node createTree(final Class<?> cls,
			final Predicate<Field> inclusionStrategy)
	{
		final RootNode root = new RootNode(cls);
		final NodeTreeConstructor constructor = new NodeTreeConstructor(
				inclusionStrategy);

		constructor.addChildren(root);

		return root;
	}

	private static Field getIndexField(final Collection<Field> fields,
			final Predicate<Field> inclusionStrategy)
	{
		for (final Field field : fields)
		{
			if (!isTableIndex(field))
				continue;

			if (!isTableIndexIsValid(field))
				continue;

			if (!isTableIndexIsIncluded(field, inclusionStrategy))

				continue;

			return field;
		}

		return null;
	}

	private static boolean isIncluded(final Field field,
			final Predicate<Field> inclusionStrategy)
	{
		final boolean table = Node.isTable(field.getType());
		final boolean include = inclusionStrategy.apply(field);

		if (include && table)
			return isTableIsAnnotated(field);
		else
			return include;
	}

	private static boolean isTableIndex(Field field)
	{
		return field.getAnnotation(SnmpTableIndex.class) != null;
	}

	private static boolean isTableIndexIsIncluded(final Field field,
			final Predicate<Field> inclusionStrategy)
	{
		final boolean include = isIncluded(field, inclusionStrategy);

		if (!include)
		{
			logger.warn(ErrorMessage.TABLE_INDEX_NOT_INCLUDED.getFmt(),
					field.getName());
		}

		return include;
	}

	private static boolean isTableIndexIsValid(final Field field)
	{
		final Class<?> type = field.getType();
		final boolean valid = type == String.class
				|| Number.class.isAssignableFrom(type);

		if (!valid)
		{
			logger.warn(ErrorMessage.TABLE_INDEX_NOT_VALID.getFmt(),
					field.getName());
		}

		return valid;
	}

	private static boolean isTableIsAnnotated(Field field)
	{
		final boolean annotation = field.getAnnotation(Jots.class) != null;

		if (!annotation)
		{
			logger.warn(ErrorMessage.COLLECTION_NO_ANNOTATION.getFmt(),
					field.getDeclaringClass(), field.getName());
		}

		return annotation;
	}

	private final Predicate<Field> inclusionStrategy;

	private NodeTreeConstructor(final Predicate<Field> inclusionStrategy)
	{
		this.inclusionStrategy = inclusionStrategy;
	}

	private void addChildren(final InnerNode parent)
	{
		addChildren(parent, Fields.getFields(parent.klass));
	}

	private void addChildren(final InnerNode parent,
			final Collection<Field> fields)
	{
		for (final Field field : fields)
		{
			if (!isIncluded(field, inclusionStrategy))
				continue;

			final Node child;
			final Class<?> fieldType = field.getType();

			if (Node.isTable(fieldType))
			{
				child = new TableNode(field, parent);
				addTableChild((TableNode) child);
			} else if (Node.isLeaf(fieldType))
			{
				child = new LeafNode(field, parent);
			} else
			{
				child = new EntryNode(field, parent);
				addChildren((EntryNode) child);
			}

			child.parent.addChild(child);
			child.snmpParent.addSnmpChild(child);
		}
	}

	private void addTableChild(final TableNode parent)
	{
		final Class<?> entryClass = parent.field.getAnnotation(Jots.class)
				.cls();
		final TableEntryNode child = new TableEntryNode(parent.field,
				entryClass, parent);

		final Collection<Field> fields = Fields.getFields(entryClass);
		child.setIndexField(getIndexField(fields, inclusionStrategy));

		child.parent.addChild(child);
		child.snmpParent.addSnmpChild(child);

		addChildren(child, fields);
	}
}
