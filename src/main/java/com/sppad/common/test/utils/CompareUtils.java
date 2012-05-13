package com.sppad.common.test.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.google.common.base.Preconditions;

/**
 * Re-inventing the wheel, for fun and science!
 */
public class CompareUtils
{
    private static final int DEFAULT_BUF_SIZE = 16;
    private static final int FILE_READ_BUF_SIZE = 4096;

    /**
     * Compares the contents of two files.
     * 
     * @param fileOne
     *            A non-directory file that exists
     * @param fileTwo
     *            Another non-directory file exists
     * @return True if the files have the same contents, false otherwise.
     * @throws FileNotFoundException
     *             If either file is not found.
     * @throws IOException
     *             If there is a problem reading from either file
     */
    public static boolean fileContentsAreSame(File fileOne, File fileTwo)
            throws FileNotFoundException, IOException
    {
        Preconditions.checkNotNull(fileOne);
        Preconditions.checkNotNull(fileTwo);
        Preconditions.checkArgument(fileOne.exists() && !fileOne.isDirectory());
        Preconditions.checkArgument(fileTwo.exists() && !fileTwo.isDirectory());

        // TODO - need to test this with symlinks and other sorts of Wizardry,
        // rely on this for ensuring the inputstreams are not the same?
        if (fileOne.equals(fileTwo))
            return true;

        if (fileOne.length() != fileTwo.length())
            return false;

        InputStream isOne = null, isTwo = null;

        try
        {
            isOne = new FileInputStream(fileOne);
            isTwo = new FileInputStream(fileTwo);
            return streamContentsAreSame(isOne, isTwo, FILE_READ_BUF_SIZE);
        }
        finally
        {
            closeWithoutException(isOne);
            closeWithoutException(isTwo);
        }
    }

    /**
     * Compares the contents of two InputStreams. The stream contents should not
     * be modified while performing this check.
     * <p>
     * If the bytes should not be read out, the mark method should be called on
     * both InputStreams before and the reset method called after making this
     * call.
     * <p>
     * This method will not return true if isOne is a wrapper around isTwo or
     * visa versa.
     * <p>
     * One option to fix this would be to mark, skip, read and reset before
     * reading the second stream. After the first block is completed, each
     * subsequent block would involve skipping, reading and reseting for each
     * stream. Since this would be fairly slow and requires that the stream
     * implements mark, this usage case is not supported.
     * 
     * @param isOne
     *            An InputStream
     * @param isTwo
     *            Another InputStream
     * @return true if isOne and isTwo contain the same bytes, false otherwise
     * @throws IOException
     *             If there was a problem while reading from either of the
     *             InputStreams
     */
    public static boolean streamContentsAreSame(InputStream isOne,
            InputStream isTwo) throws IOException
    {
        Preconditions.checkNotNull(isOne);
        Preconditions.checkNotNull(isTwo);

        return streamContentsAreSame(isOne, isTwo, DEFAULT_BUF_SIZE);
    }

    /**
     * Compares the contents of two InputStreams. The stream contents should not
     * be modified while performing this check.
     * <p>
     * If the bytes should not be read out, the mark method should be called on
     * both InputStreams before and the reset method called after making this
     * call.
     * <p>
     * This method will not return true if isOne is a wrapper around isTwo or
     * visa versa.
     * <p>
     * This method should not be used to compare streams that are being written
     * to (e.g. sockets).
     * 
     * @param isOne
     *            An InputStream
     * @param isTwo
     *            Another InputStream.
     * @param bufferSize
     *            The number of bytes to use as a buffer for reading from the
     *            InputStreams
     * @return true if isOne and isTwo have a different underlying InputStreams
     *         and contain the same bytes, false otherwise
     * @throws IOException
     *             If there was a problem while reading from either of the
     *             InputStreams
     */
    protected static boolean streamContentsAreSame(InputStream isOne,
            InputStream isTwo, int bufferSize) throws IOException
    {
        boolean same = true;

        byte[] bufOne = new byte[bufferSize];
        byte[] bufTwo = new byte[bufferSize];

        int countOne = 0, countTwo = 0;
        while (same && isOne.available() > 0)
        {
            countOne = isOne.read(bufOne);
            countTwo = 0;

            // read the same number of bytes as we read from isOne, unless we
            // have reached the end
            while (countTwo < countOne && isTwo.available() > 0)
                countTwo += isTwo.read(bufTwo, countTwo, countOne - countTwo);

            if (!Arrays.equals(bufOne, bufTwo))
                same = false;
        }

        // isTwo should be at the end (i.e., isOne is not a subsequence of isTwo)
        return same && isTwo.available() == 0;
    }

    /**
     * Calls close on an input stream, discarding any IOException that occurs.
     * This should really only be called if there is no way for the call to
     * throw an exception.
     * 
     * @param is
     *            The inputStream to close
     */
    private static void closeWithoutException(InputStream is)
    {
        try
        {
            if (is != null)
                is.close();
        }
        catch (IOException ie)
        {
            // discard
            // TODO - add logging
        }
    }
}
