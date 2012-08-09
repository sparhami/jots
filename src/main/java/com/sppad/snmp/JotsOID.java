package com.sppad.snmp;

import org.snmp4j.smi.OID;

import com.sppad.datastructures.primative.IntStack;

public class JotsOID extends OID
{
    private static final long serialVersionUID = 1588073102991378510L;

    public JotsOID(int[] prefix, IntStack staticOid, IntStack extension)
    {
	int oidSize = staticOid.size();
	int extensionSize = extension.size();
	int size = prefix.length + oidSize + extensionSize;

	int[] oidArray = new int[size];

	System.arraycopy(prefix, 0, oidArray, 0, prefix.length);
	staticOid.copyTo(oidArray, prefix.length);
	extension.copyTo(oidArray, prefix.length + oidSize);

	this.setValue(oidArray);
    }
}
