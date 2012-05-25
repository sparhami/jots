package com.sppad.datastructures.primative;

import java.util.Arrays;

public class IntStack
{
    private int[] backingArray;

    private final int incrementSize = 10;

    private int topIndex = -1;

    public IntStack()
    {
	this(10);
    }

    public IntStack(int initialCapacity)
    {
	backingArray = new int[initialCapacity];
    }

    public IntStack(IntStack srcStack)
    {
	backingArray = new int[srcStack.size()];
	topIndex = srcStack.size() - 1;
	srcStack.copyTo(backingArray, 0);
    }

    public void clear()
    {
	topIndex = -1;
    }

    public void copyFrom(int[] src, int srcPos, int length)
    {
	int currentSize = size();
	if (currentSize + length > backingArray.length)
	    increaseSize(currentSize + length + 10);

	System.arraycopy(src, srcPos, backingArray, currentSize, length);
	topIndex = currentSize + length - 1;
    }

    public void copyFrom(IntStack mintyStack)
    {
	copyFrom(mintyStack.backingArray, 0, mintyStack.size());
    }

    public void copyTo(int[] dest, int destPos)
    {
	System.arraycopy(backingArray, 0, dest, destPos, size());
    }

    @Override
    public boolean equals(Object o)
    {
	if (this == o)
	    return true;

	if (!(o instanceof IntStack))
	    return false;

	IntStack other = (IntStack) o;
	if (topIndex != other.topIndex)
	    return false;

	for (int i = 0; i <= topIndex; i++)
	    if (backingArray[i] != other.backingArray[i])
		return false;
	
	return true;
    }

    public int get(int index)
    {
	return backingArray[index];
    }

    @Override
    public int hashCode()
    {
	int result = -topIndex;
	for(int i=0; i<=topIndex; i++)
	    result = result * 31 ^ backingArray[i];

	return result;
    }

    public int peek()
    {
	return backingArray[topIndex];
    }

    public int pop()
    {
	return backingArray[topIndex--];
    }

    public void push(int value)
    {
	if (topIndex + 1 >= backingArray.length)
	    increaseSize(backingArray.length + incrementSize);

	backingArray[++topIndex] = value;
    }

    public void remove(int count)
    {
	topIndex -= count;
    }

    public void set(int index, int value)
    {
	backingArray[index] = value;
    }

    public int size()
    {
	return topIndex + 1;
    }
    
    public int[] toArray() {
	return Arrays.copyOf(backingArray, topIndex + 1);
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

    private void increaseSize(int newSize)
    {
	int[] newBackingArray = new int[newSize];
	System.arraycopy(backingArray, 0, newBackingArray, 0,
		backingArray.length);
	backingArray = newBackingArray;
    }
}
