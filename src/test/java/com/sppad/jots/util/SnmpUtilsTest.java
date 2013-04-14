package com.sppad.jots.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sppad.jots.util.SnmpUtils;

public class SnmpUtilsTest
{
  private enum TestEnum
  {
    BAR, FOO;
  }

  @Test
  public void testGetExplicitString()
  {
    assertThat(SnmpUtils.getExplicitString("hello"), is(new int[] { 5, 104,
        101, 108, 108, 111 }));
  }

  @Test
  public void testGetSetterName()
  {
    assertThat(SnmpUtils.getSetterName("hello"), is("setHello"));
  }

  @Test
  public void testGetSnmpExtension_Number()
  {
    assertThat(SnmpUtils.getSnmpExtension(3), is(new int[] { 3 }));
  }

  @Test
  public void testGetSnmpExtension_String()
  {
    assertThat(SnmpUtils.getSnmpExtension("hello"), is(new int[] { 5, 104, 101,
        108, 108, 111 }));
  }

  @Test
  public void testIsBuiltin()
  {
    assertThat(SnmpUtils.isBuiltin(Double.class), is(true));
    assertThat(SnmpUtils.isBuiltin(String.class), is(true));
  }

  @Test
  public void testIsPrimtive()
  {
    assertThat(SnmpUtils.isPrimitive(Double.TYPE), is(true));
    assertThat(SnmpUtils.isPrimitive(Character.TYPE), is(true));
  }

  @Test
  public void testIsSimple()
  {
    assertThat(SnmpUtils.isSimple(Double.class), is(true));
    assertThat(SnmpUtils.isSimple(Character.TYPE), is(true));
    assertThat(SnmpUtils.isSimple(TestEnum.class), is(true));
  }

}
