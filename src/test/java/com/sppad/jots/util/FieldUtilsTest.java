package com.sppad.jots.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

public class FieldUtilsTest
{
	@SuppressWarnings("unused")
	private class TestClass
	{
		public int fieldWithoutSetter;
		public int fieldWithSetter;

		public void setFieldWithSetter(int fieldWithSetter)
		{
			this.fieldWithSetter = fieldWithSetter;
		}
	}

	private enum TestEnum
	{
		BAR, FOO;
	}

	@Test
	public void testGetSetterForField_withoutSetter()
			throws NoSuchFieldException, SecurityException,
			NoSuchMethodException
	{
		Method actual = FieldUtils.getSetterForField(TestClass.class
				.getDeclaredField("fieldWithoutSetter"));
		Method expected = null;

		assertThat(actual, is(expected));
	}

	@Test
	public void testGetSetterForField_withSetter() throws NoSuchFieldException,
			SecurityException, NoSuchMethodException
	{
		Method actual = FieldUtils.getSetterForField(TestClass.class
				.getDeclaredField("fieldWithSetter"));
		Method expected = TestClass.class.getDeclaredMethod(
				"setFieldWithSetter", Integer.TYPE);

		assertThat(actual, is(expected));
	}

	@Test
	public void testGetSetterName()
	{
		String actual = FieldUtils.getSetterName("hello");
		String expected = "setHello";

		assertThat(actual, is(expected));
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
