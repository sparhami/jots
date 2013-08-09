package com.sppad.jots.construction.mib;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class MibInfo
{
	/**
	 * Creates a header for a MIB, prior to any entries.
	 * 
	 * @param mibName
	 *            The name of the MIB
	 * @param rootName
	 *            The name of the top level entry
	 * @param description
	 *            A description for the MIB module
	 * @param parentTree
	 *            The parent tree, e.g. enterprises
	 * @param mibTreeOid
	 *            Where in the parent tree the MIB resides
	 * @param ps
	 *            A printStream to write to
	 * @return A String containing the header info.
	 */
	public static void printHeader(final String mibName,
			final String rootName, final String description,
			final String parentTree, final int mibTreeOid, final PrintStream ps)
	{
		try
		{
			final URL importsUrl = MibInfo.class
					.getResource("/jotsMibImports.txt");

			ps.println(mibName + " DEFINITIONS ::= BEGIN\n\n");
			ps.println(Resources.toString(importsUrl, Charsets.UTF_8));

			ps.println("\n\n");
			ps.println(rootName + " MODULE-IDENTITY");
			ps.println("\tLAST-UPDATED \"200001010000Z\"");
			ps.println("\tORGANIZATION \"None\"");
			ps.println("\tCONTACT-INFO \"None\"");
			ps.print("\tDESCRIPTION \n\t\t\"");
			ps.print(description);
			ps.println("\"");
			ps.println("\t::= { " + parentTree + " " + mibTreeOid + " }");
			ps.println();
		}
		catch (IOException e)
		{
			throw new RuntimeException(
					"Internal library error: failed to read MIB defs\n");
		}
	}

	public static void printFooter(PrintStream ps)
	{
		ps.print("END");
	}
	
	private MibInfo() {
		
	}
}
