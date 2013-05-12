package com.sppad.jots.construction.mib;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sppad.jots.construction.mib.TextualConvention;

public class TextualConventionTest
{
	private static enum TestEnum
	{
		ONE, TWO, THREE
	}

	@Test
	public void testCreateTextualConvention()
	{
		String actual = TextualConvention
				.createTextualConvention(TestEnum.class);

		String expected = "TestEnum ::= TEXTUAL-CONVENTION\n"
				+ "\tSYNTAX      OCTET STRING { \"ONE\", \"TWO\", \"THREE\" }\n\n";

		assertThat(actual, is(expected));
	}

}
