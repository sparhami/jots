package com.sppad.datastructures.primative;

import org.junit.Test;

import com.sppad.datastructures.primative.IntStack;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class IntStackTest
{
    @Test
    public void testCopyFrom() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	
	int[] testArray = new int[] { 2, 3, 4 };
	testStack.copyFrom(testArray, 0, testArray.length);
	
	int[] checkArray = new int[4];
	testStack.copyTo(checkArray, 0);
	
	assertArrayEquals(checkArray, new int[] {1, 2, 3, 4});
    }
    
    @Test
    public void testCopyTo() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	testStack.push(2);
	testStack.push(3);
	
	int[] testArray = new int[3];
	testStack.copyTo(testArray, 0);
	
	assertArrayEquals(testArray, new int[] {1, 2, 3});
    }
    
    @Test
    public void testCreate() {
	IntStack testStack = new IntStack();
	
	assertThat(testStack.size(), is(0));
    }
    
    @Test
    public void testEquals_different_size_same_contents() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	testStack.push(2);
	testStack.push(3);
	IntStack secondStack = new IntStack();
	secondStack.push(1);
	secondStack.push(2);
	secondStack.push(3);
	secondStack.pop();
	
	assertThat(testStack.equals(secondStack), is(false));
    }
    
    @Test
    public void testEquals_same_contents() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	testStack.push(2);
	testStack.push(3);
	IntStack secondStack = new IntStack();
	secondStack.push(1);
	secondStack.push(2);
	secondStack.push(3);
	
	assertThat(testStack.equals(secondStack), is(true));
    }
    
    @Test
    public void testEquals_same_object() {
	IntStack testStack = new IntStack();
	IntStack secondStack = testStack;
	
	assertThat(testStack.equals(secondStack), is(true));
    }
    
    @Test
    public void testEquals_same_size_different_contents() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	testStack.push(2);
	testStack.push(3);
	IntStack secondStack = new IntStack();
	secondStack.push(1);
	secondStack.push(2);
	secondStack.push(5);
	
	assertThat(testStack.equals(secondStack), is(false));
    }
    
    @Test
    public void testEquals_wrong_type() {
	IntStack testStack = new IntStack();
	Object badObject = new Object();
	
	assertThat(testStack.equals(badObject), is(false));
    }
    
    @Test
    public void testHashcode_same_contents() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	testStack.push(2);
	testStack.push(3);
	IntStack secondStack = new IntStack();
	secondStack.push(1);
	secondStack.push(2);
	secondStack.push(4);
	secondStack.pop();
	secondStack.push(3);
	
	int testStackHashCode = testStack.hashCode();
	int secondStackHashCode = secondStack.hashCode();
	assertEquals(testStackHashCode, secondStackHashCode);
    }
    
    @Test
    public void testPeek_size() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	
	testStack.peek();
	int size = testStack.size();
	assertThat(size, is(1));
    }
    
    @Test
    public void testPeek_value() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	
	int value = testStack.peek();
	assertThat(value, is(1));
    }
    
    @Test
    public void testPop_size() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	
	testStack.pop();
	int size = testStack.size();
	assertThat(size, is(0));
    }
    
    @Test
    public void testPop_value() {
	IntStack testStack = new IntStack();
	testStack.push(1);
	
	int value = testStack.pop();
	assertThat(value, is(1));
    }
}
