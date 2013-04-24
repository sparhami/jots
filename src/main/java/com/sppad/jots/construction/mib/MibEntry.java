package com.sppad.jots.construction.mib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MibEntry extends MibSubtree
{
	@Override
	public ByteArrayOutputStream finish() throws IOException
	{
		// Close the bracket from the SEQUENCE
		entryPrintStream.println("}");
		return super.finish();
	}
}