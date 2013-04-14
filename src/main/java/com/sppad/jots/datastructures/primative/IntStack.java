package com.sppad.jots.datastructures.primative;

import java.util.Arrays;

/**
 * A stack for ints that uses a backing array to store elements. Does not do any
 * index or size checking.
 */
public class IntStack
{
  private static final int DEFAULT_INCREMENT_SIZE = 10;

  private static final int DEFAULT_STACK_SIZE = 10;

  /** The backing array that holds the stack elements */
  private int[] backingArray;

  /** The index of the top element, 1 less than the size of the stack */
  private int topIndex = -1;

  /**
   * Creates an empty stack with a default capacity of 10 elements.
   */
  public IntStack()
  {
    this(DEFAULT_STACK_SIZE);
  }

  /**
   * Creates an empty stack.
   * 
   * @param initialCapacity
   *          The initial capacity of the stack.
   */
  public IntStack(final int initialCapacity)
  {
    backingArray = new int[initialCapacity];
  }

  /**
   * Creates a stack from an existing stack.
   * 
   * @param srcStack
   *          The stack to copy the elements from.
   */
  public IntStack(final IntStack srcStack)
  {
    backingArray = new int[srcStack.size()];
    topIndex = srcStack.size() - 1;
    srcStack.copyTo(backingArray, 0);
  }

  /**
   * Clears the stack.
   */
  public void clear()
  {
    topIndex = -1;
  }

  /**
   * Adds the contents of an array to the stack.
   * 
   * @param src
   *          Where to copy from
   * @param srcPos
   *          The index to start the copy from
   * @param length
   *          How many elements to copy
   */
  public void copyFrom(final int[] src, final int srcPos, final int length)
  {
    int currentSize = size();
    if (currentSize + length > backingArray.length)
      increaseSize(currentSize + length + DEFAULT_INCREMENT_SIZE);

    System.arraycopy(src, srcPos, backingArray, currentSize, length);
    topIndex = currentSize + length - 1;
  }

  /**
   * Adds the contents of a stack on top of the stack.
   * 
   * @param mintyStack
   *          The stack to copy elements from.
   */
  public void copyFrom(final IntStack intStack)
  {
    copyFrom(intStack.backingArray, 0, intStack.size());
  }

  /**
   * Copies the contents of the stack to an array.
   * 
   * @param dest
   *          Where to copy the contents to.
   * @param destPos
   *          Where in the destination array to start copying to.
   */
  public void copyTo(final int[] dest, final int destPos)
  {
    System.arraycopy(backingArray, 0, dest, destPos, size());
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
      return true;

    if (!(o instanceof IntStack))
      return false;

    final IntStack other = (IntStack) o;
    if (topIndex != other.topIndex)
      return false;

    for (int i = 0; i <= topIndex; i++)
      if (backingArray[i] != other.backingArray[i])
        return false;

    return true;
  }

  /**
   * Gets the value from somewhere in the stack. Index validation must be done
   * by the caller.
   * 
   * @param index
   *          The index of the element to get.
   * @return The value at that index.
   */
  public int get(final int index)
  {
    return backingArray[index];
  }

  @Override
  public int hashCode()
  {
    int result = -topIndex;
    for (int i = 0; i <= topIndex; i++)
      result = result * 31 ^ backingArray[i];

    return result;
  }

  /**
   * @return The top of the stack.
   */
  public int peek()
  {
    return backingArray[topIndex];
  }

  /**
   * @return The top of the stack, removing it.
   */
  public int pop()
  {
    return backingArray[topIndex--];
  }

  /**
   * Adds an element to the top of the stack, increasing the size if necessary.
   * 
   * @param value
   *          The value to add to the stack.
   */
  public void push(final int value)
  {
    if (topIndex + 1 >= backingArray.length)
      increaseSize(backingArray.length + DEFAULT_INCREMENT_SIZE);

    backingArray[++topIndex] = value;
  }

  /**
   * Removes elements from the stack.
   * 
   * @param count
   *          How many elements to remove.
   */
  public void remove(final int count)
  {
    topIndex -= count;
  }

  /**
   * @return The number of elements in the stack.
   */
  public int size()
  {
    return topIndex + 1;
  }

  /**
   * @return A copy of the backing array for the stack.
   */
  public int[] toArray()
  {
    return Arrays.copyOf(backingArray, topIndex + 1);
  }

  /**
   * Increases the size of the backing array for the stack by the specified
   * amount.
   * 
   * @param newSize
   *          The new size for the backing array, should be larger than the
   *          current size.
   */
  private void increaseSize(final int newSize)
  {
    final int[] newBackingArray = new int[newSize];
    System.arraycopy(backingArray, 0, newBackingArray, 0, backingArray.length);
    backingArray = newBackingArray;
  }
}
