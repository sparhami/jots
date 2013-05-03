package com.sppad.jots.log;

import org.junit.Test;

public class ErrorMessageTest
{

	@Test
	public void testEnumerationMembers()
	{
		// Should not get any exceptions
		for(ErrorMessage em : ErrorMessage.values())
			em.getFmt();
	}

}
