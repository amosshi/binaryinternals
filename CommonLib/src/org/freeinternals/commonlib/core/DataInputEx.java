/*
 * DataInputEx.java    01:47, Sep 06, 2010
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.core;

import java.io.IOException;

/**
 * The <code>DataInputEx</code> interface provides for reading bytes
 * from a binary stream as <code>little-endian</code> byte order and
 * reconstructing the data into Java primitive types.
 *
 * <p>
 * Example about <code>big-endian</code> and little-<code>endian</code>:<br />
 *   <pre>int i = 0x05060708;</pre>
 * In <code>big-endian</code>:<br />
 *   <pre>  0  1  2  3</pre>
 *   <pre> 05 06 07 08</pre>
 * In <code>little-endian</code>:<br />
 *   <pre>  0  1  2  3</pre>
 *   <pre> 08 07 06 05</pre>
 * </p>
 *
 * In <code>Java</code>, multi-byte data items are always stored in
 * <code>big-endian</code> order, where the high bytes come first.
 *
 * @author Amos Shi
 * @see http://en.wikipedia.org/wiki/Endianness
 */
public interface DataInputEx {

    /**
     * Reads two input bytes and returns an <code>int</code> value in the range 
     * <code>0</code>  through <code>65535</code>.
     * Let <code>a</code> be the first byte read and <code>b</code> be the
     * second byte. The value returned is:
     * <p>
     * <pre><code>(((b &amp; 0xff) &lt;&lt; 8) | (a &amp; 0xff))
     * </code></pre>
     * </p>
     *
     * @return the unsigned 16-bit value read. 
     * @throws IOException 
     */
    int readUnsignedShort_LittleEndian() throws IOException;


    /**
     * Reads four input bytes and returns an <code>int</code> value.
     * Let <code>a-d</code> be the first through fourth bytes read.
     * The value returned is:
     * <p><pre>
     * <code>
     * (((d &amp; 0xff) &lt;&lt; 24) | ((c &amp; 0xff) &lt;&lt; 16) |
     * &#32;((b &amp; 0xff) &lt;&lt; 8) | (a &amp; 0xff))
     * </code></pre>
     *
     * @return     the <code>int</code> value read.
     * @exception  IOException   if an I/O error occurs.
     */
    int readInt_LittleEndian() throws IOException;

    /**
     * Reads four input bytes as unsigned integer and returns a
     * <code>long</code> value.
     *
     * @return the <code>long</code> value read.
     * @throws IOException 
     */
    long readUnsignedInt() throws IOException;

    /**
     * Reads four input bytes as unsigned integer and returns a
     * <code>long</code> value.
     *
     * @return the <code>long</code> value read.
     * @throws IOException
     */
    long readUnsignedInt_LittleEndian() throws IOException;


    /**
     * Reads length input bytes as an ASCII string.
     *
     * @param length Then number of bytes to read
     * @return the <code>String</code> value read.
     * @throws IOException
     */
    String readASCII(int length) throws IOException;

    /**
     * Reads until a null terminator, or the end of the buffer as an ASCII
     * String.
     *
     * @return the <code>String</code> value read.
     * @throws IOException
     */
    String readASCII() throws IOException;

    /**
     * Reads until <code>b</code>, or the end of the buffer as an ASCII
     * String.
     *
     * @param end
     * @return the <code>String</code> value read.
     * @throws IOException
     */
    String readASCIIUntil(byte end) throws IOException;

    /**
     * Read until a null terminator, or the end of the buffer as binary.
     * @return 
     * @throws java.io.IOException
     */
    byte[] readBinary() throws IOException;

    /**
     * Skip to the end of the buffer.
     * @throws java.io.IOException
     */
    void skipToEnd() throws IOException;

    /**
     * Go back for <code>i</code> bytes.
     * <p>
     * The position will be back to <code>zero</code> when <code>i</code> is
     * bigger than the current position.
     * </p>
     * 
     * @param i the positions backwards, specified in byte
     * @return  the new position
     */
    int backward(int i);

    /**
     * Backward from current position until encountering the byte <code>b</code>
     * for the first time.
     * <p>
     * For example:
     * <pre>
     *   byte array index:  0   1   2   3   4   5   6   7   8   9  10  11  12
     *   byte array data : 50  47  34  6A  1B  0A  0D  20  98  7D  54  20  0D
     *   current position:                                      *
     * </pre>
     * When passing into <code>0D</code>, the return value is <code>6</code>.
     * </p>
     *
     * @param  b target byte
     * @return the position, or <code>-1</code> if not found until the first byte
     */
    int backwardTo(byte b);

    /**
     * Backward from current position until encountering the byte array
     * <code>b</code> for the first time.
     * <p>
     * For example:
     * <pre>
     *   byte array index:  0   1   2   3   4   5   6   7   8   9  10  11  12
     *   byte array data : 50  47  34  6A  1B  20  0D  0A  98  7D  54  20  0D
     *   current position:                                      *
     * </pre>
     * When passing into <code>0D 0A</code>, the return value is <code>6</code>.
     * </p>
     *
     * @param  b target bytes
     * @return the position, or <code>-1</code> if not found until the beginning
     */
    int backwardTo(byte[] b);

    /**
     * Fly to the specified <code>position</code>.
     *
     * @param  position target position
     */
    void flyTo(int position);
}
