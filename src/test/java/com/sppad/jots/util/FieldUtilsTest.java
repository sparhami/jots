package com.sppad.jots.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FieldUtilsTest
{
	private enum TestEnum
	{
		BAR, FOO;
	}

	@Test
	public void testGetSetterName()
	{
		assertThat(FieldUtils.getSetterName("hello"), is("setHello"));
	}

	@Test
	public void testIsBuiltin()
	{
		assertThat(FieldUtils.isBuiltin(Double.class), is(true));
		assertThat(FieldUtils.isBuiltin(String.class), is(true));
	}

	@Test
	public void testIsPrimtive()
	{
		assertThat(FieldUtils.isPrimitive(Double.TYPE), is(true));
		assertThat(FieldUtils.isPrimitive(Character.TYPE), is(true));
	}

	@Test
	public void testIsSimple()
	{
		assertThat(FieldUtils.isSimple(Double.class), is(true));
		assertThat(FieldUtils.isSimple(Character.TYPE), is(true));
		assertThat(FieldUtils.isSimple(TestEnum.class), is(true));
	}
}
