package com.sppad.jots;

import org.snmp4j.smi.OID;

import com.sppad.jots.datastructures.primative.IntStack;

public class JotsOID extends OID
{
  private static final long serialVersionUID = 1588073102991378510L;

  public JotsOID(
      final int[] prefix,
      final IntStack staticOid,
      final IntStack extension)
  {
    final int oidSize = staticOid.size();
    final int extensionSize = extension.size();
    final int size = prefix.length + oidSize + extensionSize;

    final int[] oidArray = new int[size];

    System.arraycopy(prefix, 0, oidArray, 0, prefix.length);
    staticOid.copyTo(oidArray, prefix.length);
    extension.copyTo(oidArray, prefix.length + oidSize);

    this.setValue(oidArray);
  }
}
