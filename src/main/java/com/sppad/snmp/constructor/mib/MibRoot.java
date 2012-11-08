package com.sppad.snmp.constructor.mib;

public class MibRoot extends MibSubtree
{
  @Override
  protected void addSequenceEntry(String name)
  {
    // Don't want to add sequence entry for MIB root
  }
}