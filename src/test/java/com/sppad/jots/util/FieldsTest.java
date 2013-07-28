package com.sppad.jots.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.sppad.jots.annotations.Jots;

public class FieldsTest
{
	@SuppressWarnings("unused")
	private class NestedObject
	{
		public int number;
	}

	@SuppressWarnings("unused")
	private class ParentClass
	{
		public String name;
	}

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

	@SuppressWarnings("unused")
	private class TestGetFieldsClass extends ParentClass
	{
		public boolean bool;

		@Jots(cls = String.class)
		public Set<String> collection;
		public NestedObject obj;
	}

	public static Function<Field, String> getFieldName = new Function<Field, String>() {
		@Override
		public String apply(Field field)
		{
			return field.getName();
		}
	};

	@Test
	public void testGetFields()
	{
		Collection<Field> fields = Fields.getFields(TestGetFieldsClass.class);
		Collection<String> actual = Collections2
				.transform(fields, getFieldName);
		String[] expected = new String[] { "name", "bool", "collection", "obj" };

		assertThat(actual, contains(expected));
	}

	@Test
	public void testGetSetterForField_withoutSetter()
			throws NoSuchFieldException, SecurityException,
			NoSuchMethodException
	{
		Method actual = Fields.getSetterForField(TestClass.class
				.getDeclaredField("fieldWithoutSetter"));
		Method expected = null;

		assertThat(actual, is(expected));
	}

	@Test
	public void testGetSetterForField_withSetter() throws NoSuchFieldException,
			SecurityException, NoSuchMethodException
	{
		Method actual = Fields.getSetterForField(TestClass.class
				.getDeclaredField("fieldWithSetter"));
		Method expected = TestClass.class.getDeclaredMethod(
				"setFieldWithSetter", Integer.TYPE);

		assertThat(actual, is(expected));
	}

	@Test
	public void testGetSetterName()
	{
		String actual = Fields.getSetterName("hello");
		String expected = "setHello";

		assertThat(actual, is(expected));
	}

	@Test
	public void testIsBuiltin()
	{
		assertThat(Fields.isBuiltin(Double.class), is(true));
		assertThat(Fields.isBuiltin(String.class), is(true));
	}

	@Test
	public void testIsPrimtive()
	{
		assertThat(Fields.isPrimitive(Double.TYPE), is(true));
		assertThat(Fields.isPrimitive(Character.TYPE), is(true));
	}

	@Test
	public void testIsSimple()
	{
		assertThat(Fields.isSimple(Double.class), is(true));
		assertThat(Fields.isSimple(Character.TYPE), is(true));
		assertThat(Fields.isSimple(TestEnum.class), is(true));
	}

}
