package com.sppad.jots.construction;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.sppad.jots.annotations.Jots;
import com.sppad.jots.exceptions.SnmpNoMoreEntriesException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;

public class MibGeneratorTest
{
	private static final int[] prefix = new int[] { 1, 3, 6, 1, 4, 1, 100 };

	@SuppressWarnings("unused")
	private static class CollectionObject
	{
		public float floatingPoint = 3.14F;

		@Jots(cls = NestedObject.class)
		public final Collection<NestedObject> nestedCollection = ImmutableList.of(
				new NestedObject(), new NestedObject(), new NestedObject());
	}

	@SuppressWarnings("unused")
	private static class NestedObject
	{
		public int number = 5;
		public String key = "aNumber";
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
	public void testGeneration() throws SnmpNoMoreEntriesException,
			SnmpOidNotFoundException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		SnmpTreeBuilder.from(new TestObject())
				.prefix(prefix).buildMib("TEST-MIB", "Test", "enterprises", baos);
		
		System.out.println(baos.toString());
	}
}
