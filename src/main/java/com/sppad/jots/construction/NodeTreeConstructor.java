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

	static boolean checkThatTableIndexIsIncluded(final Field field,
			final Predicate<Field> inclusionStrategy)
	{
		final boolean include = include(field, inclusionStrategy);

		if (!include)
		{
			logger.warn(ErrorMessage.TABLE_INDEX_NOT_INCLUDED.getFmt(),
					field.getName());
		}

		return include;
	}

	static boolean checkThatTableIndexIsValid(final Field field)
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

	static boolean checkThatTableIsAnnotated(Field field)
	{
		final boolean annotation = field.getAnnotation(Jots.class) != null;

		if (!annotation)
		{
			logger.warn(ErrorMessage.COLLECTION_NO_ANNOTATION.getFmt(),
					field.getDeclaringClass(), field.getName());
		}

		return annotation;
	}

	static Node createTree(final Class<?> cls,
			final Predicate<Field> inclusionStrategy)
	{
		final RootNode root = new RootNode(cls);
		final NodeTreeConstructor constructor = new NodeTreeConstructor(
				inclusionStrategy);

		constructor.addChildren(root);

		return root;
	}

	static Field getIndexField(final Collection<Field> fields,
			final Predicate<Field> inclusionStrategy)
	{
		for (final Field field : fields)
		{
			if (!isTableIndex(field))
				continue;

			if (!checkThatTableIndexIsValid(field))
				continue;

			if (!checkThatTableIndexIsIncluded(field, inclusionStrategy))
				continue;

			return field;
		}

		return null;
	}

	static boolean include(final Field field,
			final Predicate<Field> inclusionStrategy)
	{
		final boolean table = Node.isTable(field.getType());
		final boolean include = inclusionStrategy.apply(field);

		if (include && table)
			return checkThatTableIsAnnotated(field);
		else
			return include;
	}

	static boolean isTableIndex(Field field)
	{
		return field.getAnnotation(SnmpTableIndex.class) != null;
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
			if (!include(field, inclusionStrategy))
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
		parent.setIndexField(getIndexField(fields, inclusionStrategy));

		child.parent.addChild(child);
		child.snmpParent.addSnmpChild(child);

		addChildren(child, fields);
	}
}
