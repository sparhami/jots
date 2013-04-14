package com.sppad.jots.constructor.mib;

public class MibRoot extends MibSubtree
{
  @Override
  protected void addSequenceEntry(final String name)
  {
    // Don't want to add sequence entry for MIB root
  }
}