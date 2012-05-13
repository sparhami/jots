package com.sppad.datastructures.primative;

public class RefStack<T>
{
    private T[] backingArray;

    private int topIndex = -1;

    private final int incrementSize = 10;

    public RefStack()
    {
	this(10);
    }


    @SuppressWarnings("unchecked")
    public RefStack(int initialCapacity)
    {
	backingArray = (T[]) new Object[initialCapacity];
    }

    public void push(T value)
    {
	if (topIndex + 1 >= backingArray.length)
	    increaseSize(backingArray.length + incrementSize);

	backingArray[++topIndex] = value;
    }

    @SuppressWarnings("unchecked")
    private void increaseSize(int newSize)
    {
	T[] newBackingArray = (T[]) new Object[newSize];
	System.arraycopy(backingArray, 0, newBackingArray, 0,
		backingArray.length);
	backingArray = newBackingArray;
    }

    public void clear()
    {
	topIndex = -1;
    }

    public void remove(int count)
    {
	topIndex -= count;
    }

    public T pop()
    {
	return backingArray[topIndex--];
    }

    public T peek()
    {
	return backingArray[topIndex];
    }

    public int size()
    {
	return topIndex + 1;
    }

    public T get(int index)
    {
	return backingArray[index];
    }

    public void set(int index, T value)
    {
	backingArray[index] = value;
    }

    @Override
    public String toString()
    {
	StringBuilder builder = new StringBuilder("[");

	for (int i = 0; i <= topIndex; i++)
	{
	    builder.append(" " + backingArray[i] + ",");
	}

	builder.setCharAt(builder.length() - 1, ' ');
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode()
    {
	int result = -topIndex;
	for (int i = 0; i <= topIndex; i++)
	    result = result * 31 ^ backingArray[i].hashCode();

	return result;
    }

    @Override
    public boolean equals(Object o)
    {
	if (this == o)
	    return true;

	if (!(o instanceof RefStack))
	    return false;

	@SuppressWarnings("unchecked")
	RefStack<T> other = (RefStack<T>) o;
	
	if (topIndex != other.topIndex)
	    return false;

	for (int i = 0; i <= topIndex; i++)
	    if (backingArray[i] != other.backingArray[i])
		return false;

	return true;
    }

    public boolean contains(T object)
    {
	for (int i = 0; i < topIndex; i++)
	    if (backingArray[i] == object)
		return true;

	return false;
    }

    public T[] values()
    {
	return backingArray;
    }
}
