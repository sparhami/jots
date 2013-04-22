package com.sppad.jots.lookup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import com.sppad.jots.exceptions.SnmpBadValueException;

public class SnmpLookupFieldTest
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
	public void testCreate_enum()
			throws SecurityException,
			NoSuchFieldException,
			NoSuchMethodException
	{
		final TestClass tc = new TestClass();
		final Field field = tc.getClass().getDeclaredField("testEnum");

		final Method method = tc.getClass().getDeclaredMethod("setTestEnum",
				TestClass.TestEnum.class);
		final SnmpLookupField testField = SnmpLookupField.create(null, field,
				tc,
				method);

		assertNotNull(testField);
	}

	@Test
	public void testGet_enum()
			throws SecurityException,
			NoSuchFieldException,
			NoSuchMethodException
	{
		final TestClass tc = new TestClass();
		final Field field = tc.getClass().getDeclaredField("testEnum");

		final Method method = tc.getClass().getDeclaredMethod("setTestEnum",
				TestClass.TestEnum.class);
		final SnmpLookupField testField = SnmpLookupField.create(null, field,
				tc,
				method);

		final String value = testField.getValue().toString();
		assertThat(value, is(tc.testEnum.toString()));
	}

	@Test
	public void testSet_enum()
			throws SecurityException,
			NoSuchFieldException,
			NoSuchMethodException
	{
		final TestClass tc = new TestClass();
		final Field field = tc.getClass().getDeclaredField("testEnum");

		final Method method = tc.getClass().getDeclaredMethod("setTestEnum",
				TestClass.TestEnum.class);
		final SnmpLookupField testField = SnmpLookupField.create(null, field,
				tc,
				method);
		testField.set(TestClass.TestEnum.BAR.toString());

		final String value = testField.getValue().toString();
		assertThat(value, is(TestClass.TestEnum.BAR.toString()));
	}

	@Test(expected = SnmpBadValueException.class)
	public void testSet_enum_badValue()
			throws SecurityException,
			NoSuchFieldException,
			NoSuchMethodException
	{
		final TestClass tc = new TestClass();
		final Field field = tc.getClass().getDeclaredField("testEnum");

		final Method method = tc.getClass().getDeclaredMethod("setTestEnum",
				TestClass.TestEnum.class);
		final SnmpLookupField testField = SnmpLookupField.create(null, field,
				tc,
				method);
		testField.set("this is a bad value");
	}
}
