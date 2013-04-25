package com.sppad.jots.constructor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.snmp4j.smi.OID;

import com.sppad.jots.SnmpTree;
import com.sppad.jots.annotations.SnmpNotSettable;
import com.sppad.jots.exceptions.SnmpBadValueException;
import com.sppad.jots.exceptions.SnmpNoMoreEntriesException;
import com.sppad.jots.exceptions.SnmpNotWritableException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;
import com.sppad.jots.exceptions.SnmpPastEndOfTreeException;
import com.sppad.jots.lookup.SnmpLookupField;

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

	private static final Comparator<SnmpLookupField> COMPARE_BY_OID = new Comparator<SnmpLookupField>()
	{
		public int compare(SnmpLookupField arg0, SnmpLookupField arg1)
		{
			return arg0.getOid().compareTo(arg1.getOid());
		}
	};

	private SnmpTree tree;

	@Before
	public void setup() throws SecurityException, NoSuchFieldException,
			NoSuchMethodException
	{
		final int[] prefix = new int[] {};
		final SortedSet<SnmpLookupField> sortSet = new TreeSet<SnmpLookupField>(
				COMPARE_BY_OID);
		final Object testObj = new testClass();

		final Field fieldOne = testObj.getClass().getField("testBoolean");
		final Field fieldTwo = testObj.getClass().getField("testBooleanObject");
		final Field fieldThree = testObj.getClass().getField("testInteger");
		final Field fieldFour = testObj.getClass().getField("testString");

		sortSet.add(SnmpLookupField.create(new OID(new int[] { 1, 1 }),
				fieldOne, testObj));
		sortSet.add(SnmpLookupField.create(new OID(new int[] { 1, 2 }),
				fieldTwo, testObj));
		sortSet.add(SnmpLookupField.create(new OID(new int[] { 1, 3 }),
				fieldThree, testObj));
		sortSet.add(SnmpLookupField.create(new OID(new int[] { 1, 4 }),
				fieldFour, testObj));

		tree = new SnmpTree(prefix, sortSet);
	}

	@Test
	public void testAddObject() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		final String boolVal = tree.get(new OID(".1.1")).getVariable()
				.toString();
		assertThat(boolVal, is("true"));
	}

	@Test(expected = SnmpOidNotFoundException.class)
	public void testGet_noSuchOID() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		tree.get(new OID(".2.1")).getOid().toString();
	}

	@Test
	public void testGetNext() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException, SnmpPastEndOfTreeException
	{
		final String oid = tree.getNext(new OID(".1.1")).getOid().toString();
		assertThat(oid, is("1.2"));
	}

	@Test
	public void testGetNextIndex() throws SecurityException,
			SnmpPastEndOfTreeException
	{
		final int index = tree.getNextIndex(new OID(".1.1"));
		assertThat(index, is(1));
	}

	@Test
	public void testGetNextIndexRoot() throws SecurityException,
			SnmpPastEndOfTreeException
	{
		final int index = tree.getNextIndex(new OID(".1"));
		assertThat(index, is(0));
	}

	@Test
	public void testGetNextRoot() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException, SnmpPastEndOfTreeException
	{
		final String oid = tree.getNext(new OID(".1")).getOid().toString();
		assertThat(oid, is("1.1"));
	}

	@Test(expected = SnmpBadValueException.class)
	public void testSetBoolean_primative_wrongType() throws SecurityException,
			SnmpNotWritableException, SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		tree.set(new OID(".1.1"), "123asfa");
	}

	@Test
	public void testSetFalse_object() throws SecurityException,
			SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpNotWritableException
	{
		tree.set(new OID(".1.2"), "false");
		final String boolVal = tree.get(new OID(".1.2")).getVariable()
				.toString();
		assertThat(boolVal, is("false"));
	}

	@Test
	public void testSetFalse_primative() throws SecurityException,
			SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpNotWritableException

	{
		tree.set(new OID(".1.1"), "false");
		final String boolVal = tree.get(new OID(".1.1")).getVariable()
				.toString();
		assertThat(boolVal, is("false"));
	}

	@Test
	public void testSetInt_primative() throws SecurityException,
			SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpNotWritableException
	{
		tree.set(new OID(".1.3"), "123");
		final String intVal = tree.get(new OID(".1.3")).getVariable()
				.toString();
		assertThat(intVal, is("123"));
	}

	@Test
	public void testSetTrue_object() throws SecurityException,
			SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpNotWritableException
	{
		tree.set(new OID(".1.2"), "true");
		String boolVal = tree.get(new OID(".1.2")).getVariable().toString();
		assertThat(boolVal, is("true"));
	}

	@Test
	public void testSetTrue_primative() throws SecurityException,
			SnmpNotWritableException, SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		tree.set(new OID(".1.1"), "true");
		final String boolVal = tree.get(new OID(".1.1")).getVariable()
				.toString();
		assertThat(boolVal, is("true"));
	}

	@Test(expected = SnmpNotWritableException.class)
	public void testSnmpNotSettable() throws SecurityException,
			SnmpNotWritableException, SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		tree.set(new OID(".1.4"), "hello world");
	}

	@Test
	public void testSnmpTreeMerge() throws SecurityException,
			NoSuchFieldException, NoSuchMethodException,
			SnmpNoMoreEntriesException, SnmpOidNotFoundException
	{

		final int[] prefix = new int[] {};
		final SortedSet<SnmpLookupField> sortSet = new TreeSet<SnmpLookupField>(
				COMPARE_BY_OID);
		final Object testObj = new testClass();

		final Field fieldOne = testObj.getClass().getField("testBoolean");
		final Field fieldTwo = testObj.getClass().getField("testBooleanObject");
		final Field fieldThree = testObj.getClass().getField("testInteger");
		final Field fieldFour = testObj.getClass().getField("testString");

		sortSet.add(SnmpLookupField.create(new OID(new int[] { 2, 1 }),
				fieldOne, testObj));
		sortSet.add(SnmpLookupField.create(new OID(new int[] { 2, 2 }),
				fieldTwo, testObj));
		sortSet.add(SnmpLookupField.create(new OID(new int[] { 2, 3 }),
				fieldThree, testObj));
		sortSet.add(SnmpLookupField.create(new OID(new int[] { 2, 4 }),
				fieldFour, testObj));

		final SnmpTree newtree = new SnmpTree(prefix, sortSet);
		final SnmpTree mergedTree = tree.mergeSnmpTrees(newtree);

		mergedTree.get(new OID(".1.3")).getVariable().toString();
		mergedTree.get(new OID(".2.4")).getVariable().toString();
	}
}
