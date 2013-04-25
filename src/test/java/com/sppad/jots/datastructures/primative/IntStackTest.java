package com.sppad.jots.datastructures.primative;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class IntStackTest
{
	@Test
	public void testCopyTo()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);
		testStack.push(2);
		testStack.push(3);

		final int[] testArray = new int[3];
		testStack.copyTo(testArray, 0);

		assertArrayEquals(testArray, new int[] { 1, 2, 3 });
	}

	@Test
	public void testCreate()
	{
		final IntStack testStack = new IntStack();

		assertThat(testStack.size(), is(0));
	}

	@Test
	public void testEquals_different_size_same_contents()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);
		testStack.push(2);
		testStack.push(3);
		final IntStack secondStack = new IntStack();
		secondStack.push(1);
		secondStack.push(2);
		secondStack.push(3);
		secondStack.pop();

		assertThat(testStack.equals(secondStack), is(false));
	}

	@Test
	public void testEquals_same_contents()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);
		testStack.push(2);
		testStack.push(3);
		final IntStack secondStack = new IntStack();
		secondStack.push(1);
		secondStack.push(2);
		secondStack.push(3);

		assertThat(testStack.equals(secondStack), is(true));
	}

	@Test
	public void testEquals_same_object()
	{
		final IntStack testStack = new IntStack();
		final IntStack secondStack = testStack;

		assertThat(testStack.equals(secondStack), is(true));
	}

	@Test
	public void testEquals_same_size_different_contents()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);
		testStack.push(2);
		testStack.push(3);
		final IntStack secondStack = new IntStack();
		secondStack.push(1);
		secondStack.push(2);
		secondStack.push(5);

		assertThat(testStack.equals(secondStack), is(false));
	}

	@Test
	public void testEquals_wrong_type()
	{
		final IntStack testStack = new IntStack();
		final Object badObject = new Object();

		assertThat(testStack.equals(badObject), is(false));
	}

	@Test
	public void testHashcode_same_contents()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);
		testStack.push(2);
		testStack.push(3);
		final IntStack secondStack = new IntStack();
		secondStack.push(1);
		secondStack.push(2);
		secondStack.push(4);
		secondStack.pop();
		secondStack.push(3);

		final int testStackHashCode = testStack.hashCode();
		final int secondStackHashCode = secondStack.hashCode();
		assertEquals(testStackHashCode, secondStackHashCode);
	}

	@Test
	public void testPeek_size()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);

		testStack.peek();
		final int size = testStack.size();
		assertThat(size, is(1));
	}

	@Test
	public void testPeek_value()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);

		final int value = testStack.peek();
		assertThat(value, is(1));
	}

	@Test
	public void testPop_size()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);

		testStack.pop();
		final int size = testStack.size();
		assertThat(size, is(0));
	}

	@Test
	public void testPop_value()
	{
		final IntStack testStack = new IntStack();
		testStack.push(1);

		final int value = testStack.pop();
		assertThat(value, is(1));
	}
}
