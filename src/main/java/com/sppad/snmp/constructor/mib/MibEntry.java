package com.sppad.snmp.constructor.mib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

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