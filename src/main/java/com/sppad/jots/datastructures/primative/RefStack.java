package com.sppad.jots.datastructures.primative;

/**
 * A stack of Objects. Unlike LinkedList or Stack, contains uses == rather than
 * the equals method.
 * 
 * @param <T>
 *            The type of the objects stored in the stack
 */
public class RefStack<T>
{
	private T[] backingArray;

	private final int incrementSize = 10;

	private int topIndex = -1;

	public RefStack()
	{
		this(10);
	}

	@SuppressWarnings("unchecked")
	public RefStack(final int initialCapacity)
	{
		backingArray = (T[]) new Object[initialCapacity];
	}

	public void clear()
	{
		topIndex = -1;
	}

	public boolean contains(final T object)
	{
		for (final T element : backingArray)
			if (element == object)
				return true;

		return false;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
			return true;

		if (!(o instanceof RefStack))
			return false;

		@SuppressWarnings("unchecked")
		final RefStack<T> other = (RefStack<T>) o;

		if (topIndex != other.topIndex)
			return false;

		for (int i = 0; i <= topIndex; i++)
			if (backingArray[i] != other.backingArray[i])
				return false;

		return true;
	}

	public T get(final int index)
	{
		return backingArray[index];
	}

	@Override
	public int hashCode()
	{
		int result = -topIndex;
		for (int i = 0; i <= topIndex; i++)
			result = result * 31 ^ backingArray[i].hashCode();

		return result;
	}

	public T peek()
	{
		return backingArray[topIndex];
	}

	public T pop()
	{
		return backingArray[topIndex--];
	}

	public void push(final T value)
	{
		if (topIndex + 1 >= backingArray.length)
			increaseSize(backingArray.length + incrementSize);

		backingArray[++topIndex] = value;
	}

	public void remove(final int count)
	{
		topIndex -= count;
	}

	public void set(final int index, final T value)
	{
		backingArray[index] = value;
	}

	public int size()
	{
		return topIndex + 1;
	}

	public T[] values()
	{
		return backingArray;
	}

	@SuppressWarnings("unchecked")
	private void increaseSize(int newSize)
	{
		final T[] newBackingArray = (T[]) new Object[newSize];
		System.arraycopy(backingArray, 0, newBackingArray, 0,
				backingArray.length);
		backingArray = newBackingArray;
	}
}
