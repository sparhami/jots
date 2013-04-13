package com.sppad.construction;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public abstract class InnerNode extends Node
{
  private static final Predicate<Field> removeSynthetic = new Predicate<Field>()
  {
    public boolean apply(final Field field)
    {
      return !field.isSynthetic();
    }
  };

  private static Collection<Field> getFields(final Class<?> klass)
  {
    final List<Field> fields = new LinkedList<Field>();

    for (Class<?> c = klass; c != Object.class; c = c.getSuperclass())
      fields.addAll(0, Arrays.asList(c.getDeclaredFields()));

    return Collections2.filter(fields, removeSynthetic);
  }

  public final Collection<Field> fields;

  public InnerNode(
      final Class<?> klass,
      final Node parent,
      final boolean inTable)
  {
    super(klass, parent, inTable);

    if (Node.isLeaf(klass))
      this.fields = Collections.emptyList();
    else
      this.fields = getFields(klass);
  }
}
