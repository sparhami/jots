package com.sppad.jots.constructor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sppad.jots.annotations.SnmpIgnore;
import com.sppad.jots.annotations.SnmpInclude;
import com.sppad.jots.constructor.SnmpTree;
import com.sppad.jots.constructor.SnmpTreeConstructor;

public class SnmpTreeConstructorTest
{

  @SuppressWarnings("unused")
  private static class FlatObject
  {
    public boolean testBoolean;
    public float testFloat;
  }

  @SuppressWarnings("unused")
  private static class IgnoreAnnotationObject
  {
    @SnmpIgnore
    public boolean testBoolean;

    public float testFloat;
  }

  @SuppressWarnings("unused")
  private static class IncludeStaticFieldObject
  {
    @SnmpInclude
    public static final float FLOAT_CONSTANT = 0.0f;

    public float someFloat = FLOAT_CONSTANT;
  }

  @SuppressWarnings("unused")
  private static class ListContainingObject
  {
    public final ImmutableList<FlatObject> testList = ImmutableList.of(
        new FlatObject(), new FlatObject());
  }

  @SuppressWarnings("unused")
  private static class MapContainingObject
  {
    public final ImmutableMap<String, FlatObject> testMap = ImmutableMap.of(
        "hello", new FlatObject(), "world", new FlatObject());
  }

  @SuppressWarnings("unused")
  private static class NestedMapObject
  {
    public final ImmutableMap<String, MapContainingObject> outerMap = ImmutableMap
        .of("one", new MapContainingObject(), "two", new MapContainingObject());
  }

  @SuppressWarnings("unused")
  private static class OneDeepObject
  {
    public final FlatObject flatObject = new FlatObject();
    public float someFloat = 0.0f;
  }

  @SuppressWarnings("unused")
  private static class StaticFieldObject
  {
    public static final float FLOAT_CONSTANT = 0.0f;

    public float someFloat = FLOAT_CONSTANT;
  }

  private static boolean getIndexAsBoolean(SnmpTree tree, int index)
  {
    return Boolean.parseBoolean(tree.get(index).toValueString());
  }

  private static float getIndexAsFloat(SnmpTree tree, int index)
  {
    return Float.parseFloat(tree.get(index).toValueString());
  }

  @Test
  public void testFlatObject()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new FlatObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(1));
    assertThat(getIndexAsBoolean(tree, 0), is(false));
    assertThat(getIndexAsFloat(tree, 1), is(0.0f));
  }

  @Test
  public void testIgnoreAnnotation()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new IgnoreAnnotationObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(0));
    assertThat(getIndexAsFloat(tree, 0), is(0.0f));
  }

  @Test
  public void testIncludeAnnotation()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new IncludeStaticFieldObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(1));
    assertThat(getIndexAsFloat(tree, 0), is(0.0f));
    assertThat(getIndexAsFloat(tree, 1), is(0.0f));
  }

  @Test
  public void testListContainingObject()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new ListContainingObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(3));
    assertThat(getIndexAsBoolean(tree, 0), is(false));
    assertThat(getIndexAsBoolean(tree, 1), is(false));
    assertThat(getIndexAsFloat(tree, 2), is(0.0f));
    assertThat(getIndexAsFloat(tree, 3), is(0.0f));
  }

  @Test
  public void testMapContainingObject()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new MapContainingObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(3));
    assertThat(getIndexAsBoolean(tree, 0), is(false));
    assertThat(getIndexAsBoolean(tree, 1), is(false));
    assertThat(getIndexAsFloat(tree, 2), is(0.0f));
    assertThat(getIndexAsFloat(tree, 3), is(0.0f));
  }

  @Test
  public void testNestedMapObject()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new NestedMapObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(7));
    assertThat(getIndexAsBoolean(tree, 0), is(false));
    assertThat(getIndexAsBoolean(tree, 1), is(false));
    assertThat(getIndexAsBoolean(tree, 2), is(false));
    assertThat(getIndexAsBoolean(tree, 3), is(false));
    assertThat(getIndexAsFloat(tree, 4), is(0.0f));
    assertThat(getIndexAsFloat(tree, 5), is(0.0f));
    assertThat(getIndexAsFloat(tree, 6), is(0.0f));
    assertThat(getIndexAsFloat(tree, 7), is(0.0f));
  }

  @Test
  public void testOneDeepObject()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new OneDeepObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(2));
    assertThat(getIndexAsBoolean(tree, 0), is(false));
    assertThat(getIndexAsBoolean(tree, 1), is(false));
    assertThat(getIndexAsFloat(tree, 2), is(0.0f));
  }

  @Test
  public void testStaticFieldObject()
      throws IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException,
      IOException
  {
    final SnmpTree tree = SnmpTreeConstructor.createSnmpTree("test", "test",
        "parent", new int[] { 1 }, new StaticFieldObject(),
        new ByteArrayOutputStream());

    assertThat(tree.lastIndex, is(0));
    assertThat(getIndexAsFloat(tree, 0), is(0.0f));
  }
}
