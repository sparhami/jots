package com.sppad.jots.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sppad.jots.util.Utils;

public class SnmpUtilsTest
{
  private enum TestEnum
  {
    BAR, FOO;
  }

  @Test
  public void testGetExplicitString()
  {
    assertThat(Utils.getExplicitString("hello"), is(new int[] { 5, 104,
        101, 108, 108, 111 }));
  }

  @Test
  public void testGetSetterName()
  {
    assertThat(Utils.getSetterName("hello"), is("setHello"));
  }

  @Test
  public void testGetSnmpExtension_Number()
  {
    assertThat(Utils.getSnmpExtension(3), is(new int[] { 3 }));
  }

  @Test
  public void testGetSnmpExtension_String()
  {
    assertThat(Utils.getSnmpExtension("hello"), is(new int[] { 5, 104, 101,
        108, 108, 111 }));
  }

  @Test
  public void testIsBuiltin()
  {
    assertThat(Utils.isBuiltin(Double.class), is(true));
    assertThat(Utils.isBuiltin(String.class), is(true));
  }

  @Test
  public void testIsPrimtive()
  {
    assertThat(Utils.isPrimitive(Double.TYPE), is(true));
    assertThat(Utils.isPrimitive(Character.TYPE), is(true));
  }

  @Test
  public void testIsSimple()
  {
    assertThat(Utils.isSimple(Double.class), is(true));
    assertThat(Utils.isSimple(Character.TYPE), is(true));
    assertThat(Utils.isSimple(TestEnum.class), is(true));
  }

}
