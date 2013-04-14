package com.sppad.jots.lookup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import com.sppad.jots.exceptions.SnmpBadValueException;
import com.sppad.jots.lookup.SnmpEnumLookupField;

public class SnmpEnumLookupFieldTest
{
  private static class TestClass
  {
    private static enum TestEnum
    {
      BAR, BAZ, FOO
    }

    public TestEnum testEnum = TestEnum.FOO;

    @SuppressWarnings("unused")
    public void setTestEnum(final TestEnum value)
    {
      this.testEnum = value;
    }
  }

  @Test
  public void testCreate()
      throws SecurityException,
      NoSuchFieldException
  {
    final TestClass tc = new TestClass();
    final Field field = tc.getClass().getDeclaredField("testEnum");

    final SnmpEnumLookupField testField = new SnmpEnumLookupField(null, field,
        tc, null);
    assertNotNull(testField);
  }

  @Test
  public void testGet()
      throws SecurityException,
      NoSuchFieldException
  {
    final TestClass tc = new TestClass();
    final Field field = tc.getClass().getDeclaredField("testEnum");

    final SnmpEnumLookupField testField = new SnmpEnumLookupField(null, field,
        tc, null);
    final String value = (String) testField.getValue();
    assertThat(value, is(tc.testEnum.toString()));
  }

  @Test
  public void testSet()
      throws SecurityException,
      NoSuchFieldException,
      NoSuchMethodException
  {
    final TestClass tc = new TestClass();
    final Field field = tc.getClass().getDeclaredField("testEnum");

    final Method method = tc.getClass().getDeclaredMethod("setTestEnum",
        TestClass.TestEnum.class);
    final SnmpEnumLookupField testField = new SnmpEnumLookupField(null, field,
        tc, method);
    testField.doSet(TestClass.TestEnum.BAR.toString());

    final String value = (String) testField.getValue();
    assertThat(value, is(TestClass.TestEnum.BAR.toString()));
  }

  @Test(expected = SnmpBadValueException.class)
  public void testSet_badValue()
      throws SecurityException,
      NoSuchFieldException,
      NoSuchMethodException
  {
    final TestClass tc = new TestClass();
    final Field field = tc.getClass().getDeclaredField("testEnum");

    final Method method = tc.getClass().getDeclaredMethod("setTestEnum",
        TestClass.TestEnum.class);
    final SnmpEnumLookupField testField = new SnmpEnumLookupField(null, field,
        tc, method);
    testField.doSet("this is a bad value");
  }
}
