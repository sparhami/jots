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
	 *            The name of the MIB
	 * @param rootName
	 *            The name of the top level entry
	 * @param description
	 *            A description for the MIB module
	 * @param parentTree
	 *            The parent tree, e.g. enterprises
	 * @param mibTreeOid
	 *            Where in the parent tree the MIB resides
	 * @return A String containing the header info.
	 */
	public static String createMibHeader(final String mibName,
											final String rootName,
											final String description,
											final String parentTree,
											final int mibTreeOid)
	{
		try
		{
			final URL importsUrl = MibInfo.class
					.getResource("/jotsMibImports.txt");

			final StringBuilder builder = new StringBuilder();
			builder.append(mibName + " DEFINITIONS ::= BEGIN\n\n");
			builder.append(Resources.toString(importsUrl, Charsets.UTF_8));

			builder.append("\n\n");
			builder.append(rootName + " MODULE-IDENTITY\n");
			builder.append("\tLAST-UPDATED \"200001010000Z\"\n");
			builder.append("\tORGANIZATION \"None\"\n");
			builder.append("\tCONTACT-INFO \"None\"\n");
			builder.append("\tDESCRIPTION \n\"");
			builder.append(description);
			builder.append("\"\n\n");
			builder.append("\t::= { " + parentTree + " " + mibTreeOid + " }\n\n");

			return builder.toString();
		} catch (IOException e)
		{
			throw new RuntimeException(
					"Internal library error: failed to read MIB defs\n");
		}
	}
}
