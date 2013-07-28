package com.sppad.jots.exceptions;

import org.snmp4j.smi.OID;

public class SnmpBadValueException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final OID oid;

	private final String value;

	public SnmpBadValueException(final OID oid, final String value)
	{
		this.oid = oid;
		this.value = value;
	}

	public OID getOid()
	{
		return oid;
	}

	public String getValue()
	{
		return value;
	}
}
