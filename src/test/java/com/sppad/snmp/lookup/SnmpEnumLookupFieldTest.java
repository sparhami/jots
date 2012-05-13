package com.sppad.snmp.lookup;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import com.sppad.snmp.lookup.SnmpEnumLookupField;

public class SnmpEnumLookupFieldTest
{
    private static class TestClass
    {
	private static enum TestEnum
	{
	    FOO, BAR, BAZ
	}

	public TestEnum testEnum = TestEnum.FOO;

	@SuppressWarnings("unused")
	public void setTestEnum(TestEnum value)
	{
	    this.testEnum = value;
	}
    }

    @Test
    public void testCreate() throws SecurityException, NoSuchFieldException
    {
	TestClass tc = new TestClass();
	Field field = tc.getClass().getDeclaredField("testEnum");

	SnmpEnumLookupField testField = new SnmpEnumLookupField(null, field,
		tc, null);
	assertNotNull(testField);
    }

    @Test
    public void testGet() throws SecurityException, NoSuchFieldException
    {
	TestClass tc = new TestClass();
	Field field = tc.getClass().getDeclaredField("testEnum");

	SnmpEnumLookupField testField = new SnmpEnumLookupField(null, field,
		tc, null);
	String value = (String) testField.get();
	assertThat(value, is(tc.testEnum.toString()));
    }

    @Test
    public void testSet() throws SecurityException, NoSuchFieldException,
	    NoSuchMethodException
    {
	TestClass tc = new TestClass();
	Field field = tc.getClass().getDeclaredField("testEnum");

	Method method = tc.getClass().getDeclaredMethod("setTestEnum",
		TestClass.TestEnum.class);
	SnmpEnumLookupField testField = new SnmpEnumLookupField(null, field,
		tc, method);
	testField.doSet(TestClass.TestEnum.BAR.toString());

	String value = (String) testField.get();
	assertThat(value, is(TestClass.TestEnum.BAR.toString()));
    }
}
