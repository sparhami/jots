package com.sppad.jots.constructor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.smi.OID;

import com.sppad.jots.annotations.SnmpNotSettable;
import com.sppad.jots.constructor.SnmpTree;
import com.sppad.jots.constructor.SnmpTreeSkeleton;
import com.sppad.jots.exceptions.SnmpBadValueException;
import com.sppad.jots.exceptions.SnmpNotWritableException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;

public class SnmpTreeTest
{
  public static class testClass
  {
    public boolean testBoolean = true;
    public Boolean testBooleanObject = true;
    public int testInteger = 0;
    @SnmpNotSettable
    public String testString = "can't set this";

    public void setTestBoolean(final boolean foo)
    {
      this.testBoolean = foo;
    }

    public void setTestBooleanObject(final Boolean foo)
    {
      this.testBooleanObject = foo;
    }

    public void setTestInteger(final int foo)
    {
      this.testInteger = foo;
    }

    public void setTestString(final String foo)
    {
      this.testString = foo;
    }
  }

  private static SnmpTree tree;

  @Before
  public void setup()
      throws SecurityException,
      NoSuchFieldException,
      NoSuchMethodException
  {
    final int[] prefix = new int[] {};
    final SnmpTreeSkeleton skel = new SnmpTreeSkeleton(prefix);

    final Object testObj = new testClass();
    final Field fieldOne = testObj.getClass().getField("testBoolean");
    final Method methodOne = testObj.getClass().getMethod("setTestBoolean",
        Boolean.TYPE);
    skel.add(new OID(new int[] { 1, 1 }), fieldOne, testObj, methodOne);
    final Field fieldTwo = testObj.getClass().getField("testBooleanObject");
    final Method methodTwo = testObj.getClass().getMethod(
        "setTestBooleanObject", Boolean.class);
    skel.add(new OID(new int[] { 1, 2 }), fieldTwo, testObj, methodTwo);
    final Field fieldThree = testObj.getClass().getField("testInteger");
    final Method methodThree = testObj.getClass().getMethod("setTestInteger",
        Integer.TYPE);
    skel.add(new OID(new int[] { 1, 3 }), fieldThree, testObj, methodThree);
    final Field fieldFour = testObj.getClass().getField("testString");
    final Method methodFour = testObj.getClass().getMethod("setTestString",
        String.class);
    skel.add(new OID(new int[] { 1, 4 }), fieldFour, testObj, methodFour);
    tree = skel.finishTreeConstruction();
  }

  @Test
  public void testAddObject()
  {
    final String boolVal = tree.get(new OID(".1.1")).getVariable().toString();
    assertThat(boolVal, is("true"));
  }

  @Test(expected = SnmpOidNotFoundException.class)
  public void testGet_noSuchOID()
  {
    tree.get(new OID(".2.1")).getOid().toString();
  }

  @Test
  public void testGetNext()
  {
    final String oid = tree.getNext(new OID(".1.1")).getOid().toString();
    assertThat(oid, is("1.2"));
  }

  @Test
  public void testGetNextIndex()
      throws SecurityException
  {
    final int index = tree.getNextIndex(new OID(".1.1"));
    assertThat(index, is(1));
  }

  @Test
  public void testGetNextIndexRoot()
      throws SecurityException
  {
    final int index = tree.getNextIndex(new OID(".1"));
    assertThat(index, is(0));
  }

  @Test
  public void testGetNextRoot()
  {
    final String oid = tree.getNext(new OID(".1")).getOid().toString();
    assertThat(oid, is("1.1"));
  }

  @Test(expected = SnmpBadValueException.class)
  public void testSetBoolean_primative_wrongType()
      throws SecurityException
  {
    tree.set(new OID(".1.1"), "123asfa");
  }

  @Test
  public void testSetFalse_object()
      throws SecurityException
  {
    tree.set(new OID(".1.2"), "false");
    final String boolVal = tree.get(new OID(".1.2")).getVariable().toString();
    assertThat(boolVal, is("false"));
  }

  @Test
  public void testSetFalse_primative()
      throws SecurityException
  {
    tree.set(new OID(".1.1"), "false");
    final String boolVal = tree.get(new OID(".1.1")).getVariable().toString();
    assertThat(boolVal, is("false"));
  }

  @Test
  public void testSetInt_primative()
      throws SecurityException
  {
    tree.set(new OID(".1.3"), "123");
    final String intVal = tree.get(new OID(".1.3")).getVariable().toString();
    assertThat(intVal, is("123"));
  }

  @Test
  public void testSetTrue_object()
      throws SecurityException
  {
    tree.set(new OID(".1.2"), "true");
    String boolVal = tree.get(new OID(".1.2")).getVariable().toString();
    assertThat(boolVal, is("true"));
  }

  @Test
  public void testSetTrue_primative()
      throws SecurityException
  {
    tree.set(new OID(".1.1"), "true");
    final String boolVal = tree.get(new OID(".1.1")).getVariable().toString();
    assertThat(boolVal, is("true"));
  }

  @Test(expected = SnmpNotWritableException.class)
  public void testSnmpNotSettable()
      throws SecurityException
  {
    tree.set(new OID(".1.4"), "hello world");
  }

  @Test
  public void testSnmpTreeMerge()
      throws SecurityException,
      NoSuchFieldException,
      NoSuchMethodException
  {

    final int[] prefix = new int[] {};
    final SnmpTreeSkeleton skel = new SnmpTreeSkeleton(prefix);

    final Object testObj = new testClass();
    final Field fieldOne = testObj.getClass().getField("testBoolean");
    final Method methodOne = testObj.getClass().getMethod("setTestBoolean",
        Boolean.TYPE);
    skel.add(new OID(new int[] { 2, 1 }), fieldOne, testObj, methodOne);
    final Field fieldTwo = testObj.getClass().getField("testBooleanObject");
    final Method methodTwo = testObj.getClass().getMethod(
        "setTestBooleanObject", Boolean.class);
    skel.add(new OID(new int[] { 2, 2 }), fieldTwo, testObj, methodTwo);
    final Field fieldThree = testObj.getClass().getField("testInteger");
    final Method methodThree = testObj.getClass().getMethod("setTestInteger",
        Integer.TYPE);
    skel.add(new OID(new int[] { 2, 3 }), fieldThree, testObj, methodThree);
    final Field fieldFour = testObj.getClass().getField("testString");
    final Method methodFour = testObj.getClass().getMethod("setTestString",
        String.class);
    skel.add(new OID(new int[] { 2, 4 }), fieldFour, testObj, methodFour);
    final SnmpTree newtree = skel.finishTreeConstruction();

    final SnmpTree mergedTree = tree.mergeSnmpTrees(newtree);

    mergedTree.get(new OID(".1.3")).getVariable().toString();
    mergedTree.get(new OID(".2.4")).getVariable().toString();
  }
}