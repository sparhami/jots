package com.sppad.jots.lookup;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.base.Function;
import com.sppad.jots.exceptions.SnmpBadValueException;

public class ValueConvertersTest
{
	private static enum Color {
		BLUE, GREEN, RED
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_boolean_bad()
	{
		ValueConverters.get(Boolean.class).apply("1241asd");
	}

	@Test
	public void test_boolean_false()
	{
		final Boolean actual = (Boolean) ValueConverters.get(Boolean.class)
				.apply("false");

		assertThat(actual, is(Boolean.FALSE));
	}

	@Test
	public void test_boolean_primitive_true()
	{
		final Boolean actual = (Boolean) ValueConverters.get(Boolean.TYPE)
				.apply("true");

		assertThat(actual, is(Boolean.TRUE));
	}

	@Test
	public void test_boolean_true()
	{
		final Boolean actual = (Boolean) ValueConverters.get(Boolean.class)
				.apply("true");

		assertThat(actual, is(Boolean.TRUE));
	}

	@Test
	public void test_double()
	{
		final Double actual = (Double) ValueConverters.get(Double.class)
				.apply("1234.0");

		assertThat(actual, is(1234.0));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_double_bad()
	{
		ValueConverters.get(Double.class).apply("asbc");
	}

	@Test
	public void test_double_primitive()
	{
		final Double actual = (Double) ValueConverters.get(Double.TYPE)
				.apply("1234");

		assertThat(actual, is(1234.0));
	}

	@Test
	public void test_enum()
	{
		final Function<String, Color> converter = ValueConverters
				.enumConverter(Color.class);
		final Color actual = (Color) converter.apply("RED");

		assertThat(actual, is(Color.RED));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_enum_bad()
	{
		final Function<String, Color> converter = ValueConverters
				.enumConverter(Color.class);
		converter.apply("purple");
	}

	@Test
	public void test_enum_cache()
	{
		final Function<String, Color> converterOne = ValueConverters
				.enumConverter(Color.BLUE.getClass());
		final Function<String, Color> converterTwo = ValueConverters
				.enumConverter(Color.RED.getClass());

		assertEquals(converterOne, converterTwo);
	}

	@Test
	public void test_float()
	{
		final Float actual = (Float) ValueConverters.get(Float.class)
				.apply("1234.0");

		assertThat(actual, is(1234.0F));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_float_bad()
	{
		ValueConverters.get(Float.class).apply("asbc");
	}

	@Test
	public void test_float_primitive()
	{
		final Float actual = (Float) ValueConverters.get(Float.TYPE)
				.apply("1234");

		assertThat(actual, is(1234.0F));
	}

	@Test
	public void test_integer()
	{
		final Integer actual = (Integer) ValueConverters.get(Integer.class)
				.apply("1234");

		assertThat(actual, is(1234));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_integer_bad()
	{
		ValueConverters.get(Integer.class).apply("asbc");
	}

	@Test
	public void test_integer_primitive()
	{
		final Integer actual = (Integer) ValueConverters.get(Integer.TYPE)
				.apply("1234");

		assertThat(actual, is(1234));
	}

	@Test
	public void test_long()
	{
		final Long actual = (Long) ValueConverters.get(Long.class)
				.apply("1234");

		assertThat(actual, is(1234L));
	}

	@Test(expected = SnmpBadValueException.class)
	public void test_long_bad()
	{
		ValueConverters.get(Long.class).apply("asbc");
	}

	@Test
	public void test_long_primitive()
	{
		final Long actual = (Long) ValueConverters.get(Long.TYPE)
				.apply("1234");

		assertThat(actual, is(1234L));
	}

	@Test
	public void test_string()
	{
		final String actual = (String) ValueConverters.get(String.class).apply(
				"test");

		assertThat(actual, is("test"));
	}
}
