package com.sppad.jots.exceptions;

import org.snmp4j.smi.OID;

public class SnmpOidNotFoundException extends Exception
{
	private static final long serialVersionUID = 1L;

	private final OID oid;

	public SnmpOidNotFoundException(OID oid)
	{
		this.oid = oid;
	}

	public OID getOid()
	{
		return oid;
	}
}
