package com.sppad.jots.construction.mib;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class MibInfo
{
  /**
   * Creates a header for a MIB, prior to any entries.
   * 
   * @param mibName
   *          The name of the MIB
   * @param rootName
   *          The name of the top level entry
   * @param description
   *          A description for the MIB module
   * @param parentTree
   *          The parent tree, e.g. enterprises
   * @param mibTreeOid
   *          Where in the parent tree the MIB resides
   * @return A String containing the header info.
   */
  public static String createMibHeader(
      final String mibName,
      final String rootName,
      final String description,
      final String parentTree,
      final int mibTreeOid)
  {
    try
    {
      final URL typesUrl = MibInfo.class.getResource("/jotsMibTypes.txt");
      final URL importsUrl = MibInfo.class.getResource("/jotsMibImports.txt");

      final StringBuilder header = new StringBuilder();
      header.append(Resources.toString(typesUrl, Charsets.UTF_8));
      header.append("\n\n");
      header.append(mibName + " DEFINITIONS ::= BEGIN\n");
      header.append(Resources.toString(importsUrl, Charsets.UTF_8));

      header.append("\n\n");
      header.append(rootName + " MODULE-IDENTITY\n");
      header.append("\tLAST-UPDATED \"200001010000Z\"\n");
      header.append("\tORGANIZATION \"None\"\n");
      header.append("\tCONTACT-INFO \"None\"\n");
      header.append("\tDESCRIPTION \n\"");
      header.append(description);
      header.append("\"\n\n");
      header.append("\t::= { " + parentTree + " " + mibTreeOid + " }\n\n");

      return header.toString();
    }
    catch (IOException e)
    {
      throw new RuntimeException(
          "Internal library error: failed to read MIB defs\n");
    }
  }
}
