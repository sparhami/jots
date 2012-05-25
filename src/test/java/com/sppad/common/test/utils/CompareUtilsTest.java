package com.sppad.common.test.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.sppad.common.test.utils.CompareUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CompareUtilsTest
{
    private static byte[] testBytes;

    private static final String testString = "this is a test array of some bytes, there should be quite a few bytes in here so that it can be tested";

    @Before
    public void setUp()
    {
	testBytes = testString.getBytes();
    }

    @Test
    public void testInputStreamCompare_different_firstLonger()
	    throws IOException
    {
	byte[] testBytesCopy = Arrays.copyOf(testBytes, testBytes.length - 1);

	InputStream isOne = new ByteArrayInputStream(testBytes, 0,
		testBytes.length);
	InputStream isTwo = new ByteArrayInputStream(testBytesCopy, 0,
		testBytesCopy.length);

	assertThat(CompareUtils.streamContentsAreSame(isOne, isTwo), is(false));
    }

    @Test
    public void testInputStreamCompare_different_sameLength()
	    throws IOException
    {
	byte[] testBytesCopy = Arrays.copyOf(testBytes, testBytes.length);
	testBytesCopy[testBytes.length / 2] -= 128;

	InputStream isOne = new ByteArrayInputStream(testBytes, 0,
		testBytes.length);
	InputStream isTwo = new ByteArrayInputStream(testBytesCopy, 0,
		testBytesCopy.length);

	assertThat(CompareUtils.streamContentsAreSame(isOne, isTwo), is(false));
    }

    @Test
    public void testInputStreamCompare_different_secondLonger()
	    throws IOException
    {
	byte[] testBytesCopy = Arrays.copyOf(testBytes, testBytes.length + 1);

	InputStream isOne = new ByteArrayInputStream(testBytes, 0,
		testBytes.length);
	InputStream isTwo = new ByteArrayInputStream(testBytesCopy, 0,
		testBytesCopy.length);

	assertThat(CompareUtils.streamContentsAreSame(isOne, isTwo), is(false));
    }

    @Test
    public void testInputStreamCompare_same() throws IOException
    {
	byte[] testBytesCopy = Arrays.copyOf(testBytes, testBytes.length);

	InputStream isOne = new ByteArrayInputStream(testBytes, 0,
		testBytes.length);
	InputStream isTwo = new ByteArrayInputStream(testBytesCopy, 0,
		testBytesCopy.length);

	assertThat(CompareUtils.streamContentsAreSame(isOne, isTwo), is(true));
    }

    /**
     * Behavior isn't defined here, just a case where the same inputStream used
     * could return false
     */
    @Test
    public void testInputStreamCompare_sameStream_falseResult() throws IOException
    {
	InputStream isOne = new ByteArrayInputStream(testBytes, 0,
		testBytes.length);
	InputStream isTwo = new PushbackInputStream(isOne);

	assertThat(CompareUtils.streamContentsAreSame(isOne, isTwo), is(false));
    }

    /**
     * Behavior isn't defined here, just a case where the same inputStream used
     * could return true
     */
    @Test
    public void testInputStreamCompare_sameStream_trueResult()
	    throws IOException
    {
	int bufSize = 2;
	int isSize = 24;

	testBytes = new byte[isSize];
	for (int i = 0; i < isSize; i += bufSize * 2)
	    for (int j = 0; j < bufSize * 2; j++)
		testBytes[i + j] = (byte) (i & 0xFF);

	InputStream isOne = new ByteArrayInputStream(testBytes, 0,
		testBytes.length);
	InputStream isTwo = new PushbackInputStream(isOne);

	assertThat(CompareUtils.streamContentsAreSame(isOne, isTwo, bufSize),
		is(true));
    }
}
