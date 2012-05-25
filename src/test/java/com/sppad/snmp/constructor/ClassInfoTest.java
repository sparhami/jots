package com.sppad.snmp.constructor;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.sppad.snmp.constructor.ClassInfo;

import static org.junit.Assert.*;

public class ClassInfoTest
{
    class testOne
    {

    }

    class testThree extends testOne
    {

    }

    class testTwo extends testOne
    {

    }

    @Test
    public void testGetLeastCommonSuperclass_commonParent()
    {
	assertEquals(ClassInfo.getLeastCommonSuperclass(testTwo.class,
		testThree.class), testOne.class);
    }

    @Test
    public void testGetLeastCommonSuperclass_list()
    {
	List<Class<?>> testList = new LinkedList<Class<?>>();
	testList.add(testThree.class);
	testList.add(testTwo.class);
	testList.add(testThree.class);

	assertEquals(ClassInfo.getLeastCommonSuperclass(testList),
		testOne.class);
    }

    @Test
    public void testGetLeastCommonSuperclass_sameclass()
    {
	assertEquals(ClassInfo.getLeastCommonSuperclass(testOne.class,
		testOne.class), testOne.class);
    }

    @Test
    public void testGetLeastCommonSuperclass_subclass()
    {
	assertEquals(ClassInfo.getLeastCommonSuperclass(testOne.class,
		testTwo.class), testOne.class);
    }
}
