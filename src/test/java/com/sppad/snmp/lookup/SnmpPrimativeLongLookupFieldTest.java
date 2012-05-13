package com.sppad.snmp.lookup;

import java.lang.reflect.Field;

import org.junit.Test;

import com.sppad.snmp.lookup.SnmpPrimativeLongLookupField;


public class SnmpPrimativeLongLookupFieldTest
{
    public class TestClass {
	
	private long testLong = 20L;

	public void setTestLong(long testLong)
	{
	    this.testLong = testLong;
	}

	public long getTestLong()
	{
	    return testLong;
	}
    }
    
    @Test
    public void testGetLong() throws SecurityException, NoSuchFieldException {
	Field f = TestClass.class.getDeclaredField("testLong");
	f.setAccessible(true);
	Object obj = new TestClass();
	
	SnmpPrimativeLongLookupField testField = new SnmpPrimativeLongLookupField(null, f, obj, null);
	
	System.out.println(testField.get());
    }
}
