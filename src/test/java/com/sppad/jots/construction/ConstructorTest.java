package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.sppad.jots.construction.Constructor;
import com.sppad.jots.construction.MibGenerator;
import com.sppad.jots.construction.Node;
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
    public String name;
  }

  @SuppressWarnings("unused")
  private class TestObject extends ParentClass
  {
    public boolean bool;

    @Jots(cls = CollectionObject.class)
    public Set<CollectionObject> collection;
    public Set<CollectionObject> nonAnnotatedCollection;
    public NestedObject obj;
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
    public Collection<NestedObject> nestedTable;
    public float floatingPoint;
  }
  
  
  @Test
  public void testCreate()
  {
    Node node = Constructor.construct(TestObject.class);
    
    MibGenerator generator = new MibGenerator(new int[] { } );
    node.accept(generator);
  }

}
