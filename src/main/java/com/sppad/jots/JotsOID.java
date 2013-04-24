package com.sppad.jots;

import org.snmp4j.smi.OID;

import com.sppad.jots.datastructures.primative.IntStack;

public class JotsOID
{
	private static final IntStack NOT_TABLE_TERMINATOR = new IntStack(
			new int[] { 0 });

	public static OID createOID(final int[] prefix, final IntStack staticOid)
	{
		final OID oid = new OID();

		final int staticSize = staticOid.size();
		final int totalSize = prefix.length + staticSize;

		final int[] oidArray = new int[totalSize];

		System.arraycopy(prefix, 0, oidArray, 0, prefix.length);
		staticOid.copyTo(oidArray, prefix.length);

		oid.setValue(oidArray);

		return oid;
	}

	private static OID createOID(final int[] prefix, final IntStack staticOid,
							final IntStack extension)
	{
		final OID oid = new OID();

		final int staticSize = staticOid.size();
		final int extensionSize = extension.size();
		final int totalSize = prefix.length + staticSize + extensionSize;

		final int[] oidArray = new int[totalSize];

		System.arraycopy(prefix, 0, oidArray, 0, prefix.length);
		staticOid.copyTo(oidArray, prefix.length);
		extension.copyTo(oidArray, prefix.length + staticSize);

		oid.setValue(oidArray);

		return oid;
	}

	public static OID createTerminalOID(int[] prefix, IntStack staticOid,
										IntStack extensionStack)
	{
		if (extensionStack.size() == 0)
			return createOID(prefix, staticOid, NOT_TABLE_TERMINATOR);
		else
			return createOID(prefix, staticOid, extensionStack);
	}
}
