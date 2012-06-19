package com.sppad.snmp.constructor.mib;

import static org.junit.Assert.*;

import org.junit.Test;

public class MibInfoTest
{

    @Test
    public void testCreateMibHeader()
    {
        String result = MibInfo.createMibHeader("A-MIB", "blah", "Something", "enterprises", 1200);
        
        System.out.println(result);
    }

}
