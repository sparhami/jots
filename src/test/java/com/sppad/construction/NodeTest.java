package com.sppad.construction;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.sppad.snmp.annotations.Jots;

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
}
