package com.sppad.jots.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SnmpUtilsTest
{
	@Test
	public void testGetExplicitString()
	{
		assertThat(SnmpUtils.getExplicitString("hello"), is(new int[] { 5, 104,
				101, 108, 108, 111 }));
	}

	@Test
	public void testGetSnmpExtension_Number()
	{
		assertThat(SnmpUtils.getSnmpExtension(3), is(new int[] { 3 }));
	}

	@Test
	public void testGetSnmpExtension_String()
	{
		assertThat(SnmpUtils.getSnmpExtension("hello"), is(new int[] { 5, 104,
				101, 108, 108, 111 }));
	}
}
