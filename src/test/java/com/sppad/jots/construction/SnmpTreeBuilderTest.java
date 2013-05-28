package com.sppad.jots.construction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.sppad.jots.JotsOID;
import com.sppad.jots.SnmpTree;
import com.sppad.jots.annotations.Jots;
import com.sppad.jots.exceptions.SnmpNoMoreEntriesException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;

public class SnmpTreeBuilderTest
{
	private static final int[] prefix = new int[] { 1, 3, 6, 1, 4, 1, 100 };

	private static final SnmpTree tree = SnmpTreeBuilder.from(new TestObject())
			.prefix(prefix).build();

	@SuppressWarnings("unused")
	private static class CollectionObject
	{
		public float floatingPoint = 3.14F;

		@Jots(cls = NestedObject.class)
		public final Collection<NestedObject> nestedTable = ImmutableList.of(
				new NestedObject(), new NestedObject(), new NestedObject());
	}

	@SuppressWarnings("unused")
	private static class NestedObject
	{
		public int number = 5;
	}

	@SuppressWarnings("unused")
	private static class ParentClass
	{
		public String name = "foobar";
	}

	@SuppressWarnings("unused")
	private static class TestObject extends ParentClass
	{
		public boolean bool;

		@Jots(cls = CollectionObject.class)
		public final Set<CollectionObject> collection = Sets.newHashSet(
				new CollectionObject(), new CollectionObject(),
				new CollectionObject());

		public final Set<CollectionObject> nonAnnotatedCollection = Sets
				.newHashSet();

		public final NestedObject obj = new NestedObject();
	}

	@Test
	public void testSuperclassField() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		final OID oid = JotsOID.createOID(prefix, new int[] { 1, 1, 0 });

		String expected = "foobar";
		String actual = tree.get(oid).getVariable().toString();

		assertThat(actual, is(expected));
	}

	@Test
	public void testSubclassField() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		final OID oid = JotsOID.createOID(prefix, new int[] { 1, 2, 0 });

		String expected = "false";
		String actual = tree.get(oid).getVariable().toString();

		assertThat(actual, is(expected));
	}

	@Test
	public void testAnnotatedCollection() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		final OID oid = JotsOID.createOID(prefix, new int[] { 1, 3, 1, 1, 2 });

		String expected = "3.14";
		String actual = tree.get(oid).getVariable().toString();

		assertThat(actual, is(expected));
	}

	@Test
	public void testNestedCollection() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		final OID oid = JotsOID.createOID(prefix, new int[] { 1, 3, 1, 2, 1, 1, 2,
				3 });

		String expected = "5";
		String actual = tree.get(oid).getVariable().toString();

		assertThat(actual, is(expected));
	}

	@Test
	public void testNestedObject() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		final OID oid = JotsOID.createOID(prefix, new int[] { 1, 4, 1, 0 });

		String expected = "5";
		String actual = tree.get(oid).getVariable().toString();

		assertThat(actual, is(expected));
	}

	//@Test
	public void testPrint() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		for (VariableBinding vb : tree)
			System.out.println(vb);
	}
}
