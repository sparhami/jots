package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sppad.jots.construction.Constructor;
import com.sppad.jots.construction.MibGenerator;
import com.sppad.jots.construction.Node;
import com.sppad.jots.datastructures.primative.IntStack;
import com.sppad.jots.annotations.Jots;

public class ConstructorTest
{
  public Function<Field, String> getFieldName = new Function<Field, String>()
  {
    public String apply(Field field)
    {
      return field.getName();
    }
  };

  @SuppressWarnings("unused")
  private class ParentClass
  {
    public String name = "foobar";
  }

  @SuppressWarnings("unused")
  private class TestObject extends ParentClass
  {
    public boolean bool;

    @Jots(cls = CollectionObject.class)
    public Set<CollectionObject> collection = Sets.newHashSet(
        new CollectionObject(), new CollectionObject(), new CollectionObject());
    public Set<CollectionObject> nonAnnotatedCollection = Sets.newHashSet();;
    public NestedObject obj = new NestedObject();
  }

  @SuppressWarnings("unused")
  private class NestedObject
  {
    public int number;
  }

  @SuppressWarnings("unused")
  private class CollectionObject
  {
    @Jots(cls = NestedObject.class)
    public Collection<NestedObject> nestedTable = Lists.newArrayList();
    public float floatingPoint;
  }

  @Test
  public void testCreate()
  {
    Node node = Constructor.createTree(TestObject.class);
    Map<Node, IntStack> staticOidMap = OidGenerator.getStaticOidParts(node);

    MibGenerator.createMib(new int[] {}, node, staticOidMap);

    TreeConstructor.create(staticOidMap, node, new TestObject());
  }

}
