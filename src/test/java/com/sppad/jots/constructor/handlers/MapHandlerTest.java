package com.sppad.jots.constructor.handlers;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sppad.jots.constructor.handlers.MapHandler;

public class MapHandlerTest
{
  public class TestObjectOne
  {
    public Map<String, TestSuperclass> testMap = new HashMap<String, TestSuperclass>();
  }

  public class TestObjectTwo<R>
  {
    Map<String, R> testMap = new HashMap<String, R>();
  }

  public class TestSubclassOne extends TestSuperclass
  {

  }

  public class TestSubclassTwo extends TestSuperclass
  {

  }

  public class TestSuperclass
  {

  }

  @Test
  public void testGetValueType_generic()
      throws SecurityException,
      NoSuchFieldException
  {
    final TestObjectTwo<TestSuperclass> obj = new TestObjectTwo<TestSuperclass>();
    obj.testMap.put("one", new TestSubclassTwo());
    obj.testMap.put("two", new TestSubclassTwo());

    final Field f = obj.getClass().getDeclaredField("testMap");
    final Class<?> klass = MapHandler.getValueType(f, obj.testMap);
    assertEquals(TestSubclassTwo.class, klass);
  }

  @Test
  public void testGetValueType_generic_parent()
      throws SecurityException,
      NoSuchFieldException
  {
    final TestObjectTwo<TestSuperclass> obj = new TestObjectTwo<TestSuperclass>();
    obj.testMap.put("one", new TestSubclassTwo());
    obj.testMap.put("two", new TestSubclassOne());

    final Field f = obj.getClass().getDeclaredField("testMap");
    final Class<?> klass = MapHandler.getValueType(f, obj.testMap);
    assertEquals(TestSuperclass.class, klass);
  }

  @Test
  public void testGetValueType_specified()
      throws SecurityException,
      NoSuchFieldException
  {
    final TestObjectOne obj = new TestObjectOne();
    obj.testMap.put("one", new TestSubclassTwo());
    obj.testMap.put("two", new TestSubclassTwo());

    final Field f = TestObjectOne.class.getDeclaredField("testMap");
    final Class<?> klass = MapHandler.getValueType(f, obj.testMap);
    assertEquals(TestSuperclass.class, klass);
  }
}
