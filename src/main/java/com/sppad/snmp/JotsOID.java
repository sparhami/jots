package com.sppad.snmp;

import org.snmp4j.smi.OID;

import com.sppad.datastructures.primative.IntStack;

public class JotsOID extends OID
{
    private static final long serialVersionUID = 1588073102991378510L;

    public JotsOID(int[] prefix, IntStack staticOid)
    {
	int oidSize = staticOid.size();
	int size = prefix.length + oidSize;

	int[] oidArray = new int[size + 1];

	System.arraycopy(prefix, 0, oidArray, 0, prefix.length);
	staticOid.copyTo(oidArray, prefix.length);
	oidArray[size] = 0;

	this.setValue(oidArray);
    }

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

    public JotsOID(IntStack staticOid, IntStack extension)
    {
	int size = staticOid.size() + extension.size();
	int[] oidArray = new int[size];

	staticOid.copyTo(oidArray, 0);
	extension.copyTo(oidArray, staticOid.size());

	this.setValue(oidArray);
    }
}
