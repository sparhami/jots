package com.sppad.jots.lookup;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.base.Function;
import com.sppad.jots.exceptions.SnmpBadValueException;

public class ValueParsersTest
{
	private static enum Color
	{
		BLUE, GREEN, RED
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_boolean_bad()
	{
		ValueParsers.get(Boolean.class).apply("1241asd");
	}

	@Test
	public void test_boolean_false()
	{
		final Boolean actual = (Boolean) ValueParsers.get(Boolean.class)
				.apply("false");

		assertThat(actual, is(Boolean.FALSE));
	}

	@Test
	public void test_boolean_primitive_true()
	{
		final Boolean actual = (Boolean) ValueParsers.get(Boolean.TYPE)
				.apply("true");

		assertThat(actual, is(Boolean.TRUE));
	}

	@Test
	public void test_boolean_true()
	{
		final Boolean actual = (Boolean) ValueParsers.get(Boolean.class)
				.apply("true");

		assertThat(actual, is(Boolean.TRUE));
	}

	@Test
	public void test_double()
	{
		final Double actual = (Double) ValueParsers.get(Double.class).apply(
				"1234.0");

		assertThat(actual, is(1234.0));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_double_bad()
	{
		ValueParsers.get(Double.class).apply("asbc");
	}

	@Test
	public void test_double_primitive()
	{
		final Double actual = (Double) ValueParsers.get(Double.TYPE).apply(
				"1234");

		assertThat(actual, is(1234.0));
	}

	@Test
	public void test_enum()
	{
		final Color actual = (Color) ValueParsers.get(Color.class).apply(
				"RED");

		assertThat(actual, is(Color.RED));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_enum_bad()
	{
		ValueParsers.get(Color.class).apply("purple");
	}

	@Test
	public void test_enum_cache()
	{
		final Function<?, ?> first = ValueParsers.get(Color.BLUE.getClass());
		final Function<?, ?> second = ValueParsers.get(Color.RED.getClass());

		assertEquals(first, second);
	}

	@Test
	public void test_float()
	{
		final Float actual = (Float) ValueParsers.get(Float.class).apply(
				"1234.0");

		assertThat(actual, is(1234.0F));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_float_bad()
	{
		ValueParsers.get(Float.class).apply("asbc");
	}

	@Test
	public void test_float_primitive()
	{
		final Float actual = (Float) ValueParsers.get(Float.TYPE).apply(
				"1234");

		assertThat(actual, is(1234.0F));
	}

	@Test
	public void test_integer()
	{
		final Integer actual = (Integer) ValueParsers.get(Integer.class)
				.apply("1234");

		assertThat(actual, is(1234));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_integer_bad()
	{
		ValueParsers.get(Integer.class).apply("asbc");
	}

	@Test
	public void test_integer_primitive()
	{
		final Integer actual = (Integer) ValueParsers.get(Integer.TYPE)
				.apply("1234");

		assertThat(actual, is(1234));
	}

	@Test
	public void test_long()
	{
		final Long actual = (Long) ValueParsers.get(Long.class)
				.apply("1234");

		assertThat(actual, is(1234L));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_long_bad()
	{
		ValueParsers.get(Long.class).apply("asbc");
	}

	@Test
	public void test_long_primitive()
	{
		final Long actual = (Long) ValueParsers.get(Long.TYPE).apply("1234");

		assertThat(actual, is(1234L));
	}

	@Test
	public void test_string()
	{
		final String actual = (String) ValueParsers.get(String.class).apply(
				"test");

		assertThat(actual, is("test"));
	}
}
