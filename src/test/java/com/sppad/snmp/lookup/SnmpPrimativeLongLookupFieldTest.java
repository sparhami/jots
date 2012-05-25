package com.sppad.snmp.lookup;

import java.lang.reflect.Field;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import com.sppad.snmp.exceptions.SnmpBadValueException;
import com.sppad.snmp.lookup.SnmpPrimativeLongLookupField;


public class SnmpPrimativeLongLookupFieldTest
{
    public class TestClass {
	
	private long testLong = 20L;

	public long getTestLong()
	{
	    return testLong;
	}

	public void setTestLong(long testLong)
	{
	    this.testLong = testLong;
	}
    }
    
    @Test
    public void testGet() throws SecurityException, NoSuchFieldException {
	Field f = TestClass.class.getDeclaredField("testLong");
	f.setAccessible(true);
	Object obj = new TestClass();
	
	SnmpPrimativeLongLookupField testField = new SnmpPrimativeLongLookupField(null, f, obj, null);
	
	assertThat((Long)testField.get(), is(20L));
    }
    
    @Test
    public void testSet() throws SecurityException, NoSuchFieldException {
        Field f = TestClass.class.getDeclaredField("testLong");
        f.setAccessible(true);
        Object obj = new TestClass();
        
        SnmpPrimativeLongLookupField testField = new SnmpPrimativeLongLookupField(null, f, obj, null);
        
        testField.doSet("42");
        
        assertThat((Long)testField.get(), is(42L));
    }
    
    @Test(expected=SnmpBadValueException.class)
    public void testSet_badValue() throws SecurityException, NoSuchFieldException {
        Field f = TestClass.class.getDeclaredField("testLong");
        f.setAccessible(true);
        Object obj = new TestClass();
        
        SnmpPrimativeLongLookupField testField = new SnmpPrimativeLongLookupField(null, f, obj, null);
        
        testField.doSet("bad!");
    }
    
}
