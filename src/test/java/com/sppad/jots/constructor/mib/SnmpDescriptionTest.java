package com.sppad.jots.constructor.mib;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;

import com.sppad.jots.construction.mib.SnmpDescription;
import com.sppad.jots.mib.BooleanInterfaceComment;

public class SnmpDescriptionTest
{
	@Test
	public void testGetBooleanDescription_class() throws SecurityException,
			NoSuchFieldException
	{
		final Object obj = new Object()
		{
			@BooleanInterfaceComment(	synopsis = "This is something about the field",
										trueSynopsis = "This is a true comment",
										falseSynopsis = "This is a false comment")
			public final Boolean testBoolean = true;
		};

		final String expectedResult = "This is something about the field"
				+ "\n\t\t 'true'  -> This is a true comment"
				+ "\n\t\t 'false' -> This is a false comment";

		final Field testField = obj.getClass().getDeclaredField("testBoolean");
		final String actualResult = SnmpDescription.getDescription(testField);

		assertThat(actualResult, is(expectedResult));
	}

	@Test
	public void testGetBooleanDescription_type() throws SecurityException,
			NoSuchFieldException
	{
		final Object obj = new Object()
		{
			@BooleanInterfaceComment(	synopsis = "This is something about the field",
										trueSynopsis = "This is a true comment",
										falseSynopsis = "This is a false comment")
			public final boolean testBoolean = true;
		};

		final String expectedResult = "This is something about the field"
				+ "\n\t\t 'true'  -> This is a true comment"
				+ "\n\t\t 'false' -> This is a false comment";

		final Field testField = obj.getClass().getDeclaredField("testBoolean");
		final String actualResult = SnmpDescription.getDescription(testField);

		assertThat(actualResult, is(expectedResult));
	}
}
