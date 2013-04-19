package com.sppad.jots.construction;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.sppad.jots.annotations.Jots;
import com.sppad.jots.construction.RootNode;

public class NodeTest
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

    @Jots(cls = String.class)
    public Set<String> collection;
    public NestedObject obj;
  }

  @SuppressWarnings("unused")
  private class NestedObject
  {
    public int number;
  }

  @Test
  public void testFields()
  {
    Collection<Field> fields = new RootNode(TestObject.class).fields;
    Collection<String> actual = Collections2.transform(fields, getFieldName);
    String[] expected = new String[] { "name", "bool", "collection", "obj" };

    assertThat(actual.size(), is(expected.length));
    assertThat(actual, contains(expected));
  }
  
  @Test
  public void testIsCollection_Collection() {
    assertThat(Node.isTable(Collection.class), is(true));
  }
  
  @Test
  public void testIsCollection_List() {
    assertThat(Node.isTable(List.class), is(true));
  }
  
  @Test
  public void testIsCollection_Map() {
    assertThat(Node.isTable(Map.class), is(true));
  }
  
  @Test
  public void testIsCollection_Set() {
    assertThat(Node.isTable(Set.class), is(true));
  }
}
