package com.sppad.common.test.utils;

public class MemUtils
{
    public static Runtime runtime = Runtime.getRuntime();

    public static long getMemoryUsage()
    {
	System.gc();
	System.gc();
	System.gc();
	System.gc();
	System.gc();
	System.gc();
	return runtime.totalMemory() - runtime.freeMemory();
    }
}
