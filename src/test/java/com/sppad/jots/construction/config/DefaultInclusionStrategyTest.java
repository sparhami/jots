package com.sppad.jots.construction.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;

import com.sppad.jots.construction.config.SimpleInclusionStrategy.SnmpExclude;
import com.sppad.jots.construction.config.SimpleInclusionStrategy.SnmpInclude;

public class DefaultInclusionStrategyTest
{
	public static class TestClass
	{

		public static final Object testStaticFinalObject = null;

		public static int testStaticInt;

		public static Object testStaticObject;

		@SnmpInclude
		@SnmpExclude
		public Object testAnnotatedBoth;

		@SnmpExclude
		public final Object testAnnotatedExclude = null;
		
		@SnmpInclude
		public Object testAnnotatedInclude;
		
		public TestEnum testEnum;
		
		public final Object testFinalObject = null;

		public int testInt;
		
		public transient final Object testTransientFinalObject = null;
		
		public transient int testTransientInt;
	}

	public static enum TestEnum
	{

	}

	private final SimpleInclusionStrategy strategy = new DefaultInclusionStrategy();

	@Test
	public void testApply_object_exclude() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testAnnotatedExclude");

		boolean expected = strategy.apply(field);
		boolean actual = false;
		assertThat(expected, is(actual));
	}

	@Test
	public void testApply_object_final() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testFinalObject");

		boolean expected = strategy.apply(field);
		boolean actual = true;
		assertThat(expected, is(actual));
	}
	
	@Test
	public void testApply_object_include() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testAnnotatedInclude");

		boolean expected = strategy.apply(field);
		boolean actual = true;
		assertThat(expected, is(actual));
	}
	
	@Test
	public void testApply_object_includeAndExclude() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testAnnotatedBoth");

		boolean expected = strategy.apply(field);
		boolean actual = true;
		assertThat(expected, is(actual));
	}
	
	@Test
	public void testApply_object_static() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testStaticObject");

		boolean expected = strategy.apply(field);
		boolean actual = false;
		assertThat(expected, is(actual));
	}
	
	@Test
	public void testApply_object_static_final() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testStaticFinalObject");

		boolean expected = strategy.apply(field);
		boolean actual = false;
		assertThat(expected, is(actual));
	}

	@Test
	public void testApply_object_transientFinal() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testTransientFinalObject");

		boolean expected = strategy.apply(field);
		boolean actual = false;
		assertThat(expected, is(actual));
	}

	@Test
	public void testApply_simple_enum() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testEnum");

		boolean expected = strategy.apply(field);
		boolean actual = true;
		assertThat(expected, is(actual));
	}
	
	@Test
	public void testApply_simple_int() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testInt");

		boolean expected = strategy.apply(field);
		boolean actual = true;
		assertThat(expected, is(actual));
	}

	
	@Test
	public void testApply_simple_static() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testStaticInt");

		boolean expected = strategy.apply(field);
		boolean actual = false;
		assertThat(expected, is(actual));
	}


	@Test
	public void testApply_simple_transient() throws NoSuchFieldException,
			SecurityException
	{
		Field field = TestClass.class.getField("testTransientInt");

		boolean expected = strategy.apply(field);
		boolean actual = false;
		assertThat(expected, is(actual));
	}
	
	
}
