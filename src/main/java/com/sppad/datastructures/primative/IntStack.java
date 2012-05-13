package com.sppad.datastructures.primative;

import java.util.Arrays;

public class IntStack
{
    private int[] backingArray;

    private int topIndex = -1;

    private final int incrementSize = 10;

    public IntStack()
    {
	this(10);
    }

    public IntStack(IntStack srcStack)
    {
	backingArray = new int[srcStack.size()];
	topIndex = srcStack.size() - 1;
	srcStack.copyTo(backingArray, 0);
    }

    public IntStack(int initialCapacity)
    {
	backingArray = new int[initialCapacity];
    }

    public void push(int value)
    {
	if (topIndex + 1 >= backingArray.length)
	    increaseSize(backingArray.length + incrementSize);

	backingArray[++topIndex] = value;
    }

    private void increaseSize(int newSize)
    {
	int[] newBackingArray = new int[newSize];
	System.arraycopy(backingArray, 0, newBackingArray, 0,
		backingArray.length);
	backingArray = newBackingArray;
    }

    public void copyTo(int[] dest, int destPos)
    {
	System.arraycopy(backingArray, 0, dest, destPos, size());
    }

    public void copyFrom(int[] src, int srcPos, int length)
    {
	int currentSize = size();
	if (currentSize + length > backingArray.length)
	    increaseSize(currentSize + length + 10);

	System.arraycopy(src, srcPos, backingArray, currentSize, length);
	topIndex = currentSize + length - 1;
    }

    public void clear()
    {
	topIndex = -1;
    }

    public void remove(int count)
    {
	topIndex -= count;
    }

    public int pop()
    {
	return backingArray[topIndex--];
    }

    public int peek()
    {
	return backingArray[topIndex];
    }

    public int size()
    {
	return topIndex + 1;
    }

    public int get(int index)
    {
	return backingArray[index];
    }

    public void set(int index, int value)
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
    
    public int[] toArray() {
	return Arrays.copyOf(backingArray, topIndex + 1);
    }

    @Override
    public int hashCode()
    {
	int result = -topIndex;
	for(int i=0; i<=topIndex; i++)
	    result = result * 31 ^ backingArray[i];

	return result;
    }

    public void copyFrom(IntStack mintyStack)
    {
	copyFrom(mintyStack.backingArray, 0, mintyStack.size());
    }
}
