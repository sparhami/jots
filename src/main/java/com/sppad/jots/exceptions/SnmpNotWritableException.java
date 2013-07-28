package com.sppad.jots.exceptions;

import org.snmp4j.smi.OID;

public class SnmpNotWritableException extends SnmpException
{
	private static final long serialVersionUID = 1L;

	public SnmpNotWritableException(OID oid)
	{
		super(oid);
	}
}
