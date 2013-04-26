package com.sppad.jots.construction.config;

/**
 * The default strategy for determining what fields to include in a generated
 * SnmpTree. A SimpleInclusionStrategy that ignores non-final, static and
 * transient fields.
 * 
 * @see SimpleInclusionStrategy
 */
public class DefaultInclusionStrategy extends SimpleInclusionStrategy
{
	@Override
	protected boolean includeNonFinal()
	{
		return false;
	}

	@Override
	protected boolean includeStatic()
	{
		return false;
	}

	@Override
	protected boolean includeTransient()
	{
		return false;
	}
}
