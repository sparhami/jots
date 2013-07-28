package com.sppad.jots.construction.mib;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.base.Joiner;

public class TextualConventionTest
{
	private static enum TestEnum
	{
		ONE, THREE, TWO
	}

	@Test
	public void testCreateTextualConvention()
	{
		String actual = TextualConvention
				.createTextualConvention(TestEnum.class);

		String expected = "TestEnum ::= TEXTUAL-CONVENTION\n\tSYNTAX      OCTET STRING { \"";
		expected += Joiner.on("\", \"").join(TestEnum.values());
		expected += "\" }\n\n";

		assertThat(actual, is(expected));
	}
}
