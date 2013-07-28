package com.sppad.jots.exceptions;

import org.snmp4j.smi.OID;

public class SnmpException extends Exception
{
	private static final long serialVersionUID = 1L;

	private final OID oid;

	public SnmpException(OID oid)
	{
		this.oid = oid;
	}

	public OID getOid()
	{
		return oid;
	}
}
