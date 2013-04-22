package com.sppad.jots.construction;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.sppad.jots.annotations.Jots;

public class NodeTest
{
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
  public void testIsCollection_Collection()
  {
    assertThat(Node.isTable(Collection.class), is(true));
  }

  @Test
  public void testIsCollection_List()
  {
    assertThat(Node.isTable(List.class), is(true));
  }

  @Test
  public void testIsCollection_Map()
  {
    assertThat(Node.isTable(Map.class), is(true));
  }

  @Test
  public void testIsCollection_Set()
  {
    assertThat(Node.isTable(Set.class), is(true));
  }
}
