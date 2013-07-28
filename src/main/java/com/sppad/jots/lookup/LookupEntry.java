package com.sppad.jots.lookup;

import org.snmp4j.smi.OID;

public class LookupEntry implements Comparable<LookupEntry>
{
	final OID oid;

	public LookupEntry(OID oid)
	{
		this.oid = oid;
	}

	@Override
	public int compareTo(LookupEntry o)
	{
		return oid.compareTo(o.oid);
	}

	public OID getOid()
	{
		return oid;
	}
}
