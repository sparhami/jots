package com.sppad.snmp.lookup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;

import com.sppad.snmp.exceptions.SnmpBadValueException;

public class SnmpPrimativeLongLookupFieldTest
{
  public class TestClass
  {

    private long testLong = 20L;

    public long getTestLong()
    {
      return testLong;
    }

    public void setTestLong(long testLong)
    {
      this.testLong = testLong;
    }
  }

  @Test
  public void testGet()
      throws SecurityException,
      NoSuchFieldException
  {
    final Field f = TestClass.class.getDeclaredField("testLong");
    f.setAccessible(true);
    final Object obj = new TestClass();

    final SnmpPrimativeLongLookupField testField = new SnmpPrimativeLongLookupField(
        null, f, obj, null);

    assertThat((Long) testField.get(), is(20L));
  }

  @Test
  public void testSet()
      throws SecurityException,
      NoSuchFieldException
  {
    final Field f = TestClass.class.getDeclaredField("testLong");
    f.setAccessible(true);
    final Object obj = new TestClass();

    final SnmpPrimativeLongLookupField testField = new SnmpPrimativeLongLookupField(
        null, f, obj, null);

    testField.doSet("42");

    assertThat((Long) testField.get(), is(42L));
  }

  @Test(expected = SnmpBadValueException.class)
  public void testSet_badValue()
      throws SecurityException,
      NoSuchFieldException
  {
    final Field f = TestClass.class.getDeclaredField("testLong");
    f.setAccessible(true);
    final Object obj = new TestClass();

    final SnmpPrimativeLongLookupField testField = new SnmpPrimativeLongLookupField(
        null, f, obj, null);

    testField.doSet("bad!");
  }

}
