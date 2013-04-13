package com.sppad.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.sppad.snmp.annotations.Jots;

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
    RootNode node = Constructor.create(TestObject.class);
    
    MibGenerator generator = new MibGenerator(new int[] { } );
    node.accept(generator);
  }

}
