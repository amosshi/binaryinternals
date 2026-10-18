/*
 * PosDataInputStream.java    August 8, 2007, 12:48 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib.core;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;

/// @author Amos Shi
public class PosDataInputStream extends DataInputStream implements DataInputEx {

    public static final int USHORT_LENGTH = 2;
    public static final byte[] EMPTY_BYTE_ARRAY = {};

    /// Number in bytes for Java `byte` type.
    public static final int LENGTH_BYTE = 1;
    /// Number in bytes for Java `short` type.
    public static final int LENGTH_SHORT = 2;
    /// Number in bytes for Java `int` type.
    public static final int LENGTH_INT = 4;
    /// Number in bytes for Java `long` type.
    public static final int LENGTH_LONG = 8;

    /// Shift Operators, offset with 8.
    protected static final int SHIFT_8 = 8;
    /// Shift Operators, offset with 16.
    protected static final int SHIFT_16 = 16;
    /// Shift Operators, offset with 24.
    protected static final int SHIFT_24 = 24;
    /// Shift Operators, offset with 32.
    protected static final int SHIFT_32 = 32;
    /// Shift Operators, offset with 40.
    protected static final int SHIFT_40 = 40;
    /// Shift Operators, offset with 48.
    protected static final int SHIFT_48 = 48;
    /// Shift Operators, offset with 56.
    protected static final int SHIFT_56 = 56;
    /// Half Byte length: 4.
    protected static final int BYTE_LENGTH_4 = 4;
    /// Full Byte length: 8.
    protected static final int BYTE_LENGTH_8 = 8;

    /// Byte offset 0.
    protected static final int BYTE_OFFSET_0 = 0;
    /// Byte offset 1.
    protected static final int BYTE_OFFSET_1 = 1;
    /// Byte offset 2.
    protected static final int BYTE_OFFSET_2 = 2;
    /// Byte offset 3.
    protected static final int BYTE_OFFSET_3 = 3;
    /// Byte offset 4.
    protected static final int BYTE_OFFSET_4 = 4;
    /// Byte offset 5.
    protected static final int BYTE_OFFSET_5 = 5;
    /// Byte offset 6.
    protected static final int BYTE_OFFSET_6 = 6;
    /// Byte offset 7.
    protected static final int BYTE_OFFSET_7 = 7;
    /// Byte max value: 255.
    protected static final int BYTE_MAX_255 = 255;

    /// New line character: LINE FEED (LF).
    public static final byte NEWLINE_LF = 0x0A;
    /// New line character: CARRIAGE RETURN (CR).
    public static final byte NEWLINE_CR = 0x0D;

    /// Mask of the lower 64 bits, used to read a `long` as unsigned.
    private static final BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);

    /// Offset of the 1st byte.
    protected int offset = 0;

    /// The underlying stream, same as {@link #in} but with position access.
    private final PosByteArrayInputStream posIn;

    /// Creates a new instance of PosDataInputStream.
    ///
    /// @param in Binary data input stream
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Wraps the stream, as DataInputStream does")
    public PosDataInputStream(final PosByteArrayInputStream in) {
        super(in);
        this.posIn = in;
    }

    /// Create a sub {@link PosDataInputStream}, which starts from
    /// `offset`.
    ///
    /// @param in Binary data input stream
    /// @param offset Offset of the stream
    public PosDataInputStream(final PosByteArrayInputStream in, int offset) {
        this(in);
        this.offset = offset;
    }

    /// Get a partial {@link PosDataInputStream}, which starts from
    /// `startPos` of original stream, with length `length`.
    ///
    /// @param startPos Start position
    /// @param length Length
    /// @return A partial {@link PosDataInputStream} object
    public PosDataInputStream getPartialStream(final int startPos, final int length) {
        return new PosDataInputStream(
                new PosByteArrayInputStream(this.getBuf(startPos, length)),
                startPos);
    }

    /// Get the absolute position of the starting point of the buffer.
    ///
    /// @return buffer absolute position
    public int getOffset() {
        return this.offset;
    }

    /// Get current absolute position of the file.
    ///
    /// @return The index of the next character to read from the input stream
    /// buffer, plus {@link #getOffset()}
    public int getPos() {
        return this.posIn.getPos() + this.offset;
    }

    /// Get the byte array buffer of the input stream.
    ///
    /// @return the byte array
    public byte[] getBuf() {
        return this.posIn.getBuf();
    }

    /// Return a part of the byte array.
    ///
    /// @param startPos Start Position of the original byte array
    /// @param length Length of data to be read
    /// @return the partial byte array
    public byte[] getBuf(final int startPos, final int length) {
        if ((startPos < 0) || (length < 1)) {
            throw new IllegalArgumentException("startIndex or length is not valid. startIndex = " + startPos + ", length = " + length);
        }

        final byte[] bufFull = this.getBuf();
        Objects.checkFromIndexSize(startPos, length, bufFull.length);
        return Arrays.copyOfRange(bufFull, startPos, startPos + length);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods
    @Override
    public short readShortInLittleEndian() throws IOException {
        return Short.reverseBytes(this.readShort());
    }

    @Override
    public int readUnsignedShortInLittleEndian() throws IOException {
        return Short.toUnsignedInt(this.readShortInLittleEndian());
    }

    @Override
    public int readIntInLittleEndian() throws IOException {
        return Integer.reverseBytes(this.readInt());
    }

    @Override
    public long readUnsignedInt() throws IOException {
        return Integer.toUnsignedLong(this.readInt());
    }

    @Override
    public long readUnsignedIntInLittleEndian() throws IOException {
        return Integer.toUnsignedLong(this.readIntInLittleEndian());
    }

    @Override
    public long readLongInLittleEndian() throws IOException {
        return Long.reverseBytes(this.readLong());
    }

    @Override
    public BigInteger readUnsignedLong() throws IOException {
        return toUnsignedBigInteger(this.readLong());
    }

    @Override
    public BigInteger readUnsignedLongInLittleEndian() throws IOException {
        return toUnsignedBigInteger(this.readLongInLittleEndian());
    }

    private static BigInteger toUnsignedBigInteger(final long value) {
        return BigInteger.valueOf(value).and(UNSIGNED_LONG_MASK);
    }

    @Override
    public String readASCII(final int length) throws IOException {
        if (length <= 0) {
            throw new IllegalArgumentException(
                    "Parameter length should be greater than 0. (length = %d)".formatted(length));
        }

        final byte[] bytes = new byte[length];
        this.readFully(bytes);
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    @Override
    public String readASCII() throws IOException {
        return this.readASCIIUntil((byte) 0);
    }

    /// Read current byte array as ASCII string until `byte` `end`.
    ///
    /// @param end End character, it is consumed but not included in the result
    /// @return ASCII as string
    @Override
    public String readASCIIUntil(final byte end) {
        return this.readASCIIUntil(b -> b == end);
    }

    /// Read current byte array as ASCII string until any `byte` in
    /// array `end`.
    ///
    /// @param end End values for the ASCII string, the matched one is consumed
    /// but not included in the result
    /// @return ASCII as string
    public String readASCIIUntil(final byte... end) {
        if (end == null || end.length < 1) {
            throw new IllegalArgumentException("Invalid parameter 'end'.");
        }

        return this.readASCIIUntil(b -> contains((byte) b, end));
    }

    private String readASCIIUntil(final IntPredicate isEnd) {
        final int start = this.posIn.getPos();
        final int stop = this.consumeUntil(isEnd);
        return new String(this.getBuf(), start, stop - start, StandardCharsets.ISO_8859_1);
    }

    /// Read current byte array as ASCII string until a {@link #NEWLINE_CR} /
    /// {@link #NEWLINE_LF} flag found.
    ///
    /// @return ASCII Line
    /// @throws IOException Read failed
    public ASCIILine readASCIILine() throws IOException {
        int nlLen = 1;
        String line = this.readASCIIUntil(NEWLINE_CR, NEWLINE_LF);
        if (this.hasNext()) {
            byte next = this.readByte();
            if (next != NEWLINE_LF && next != NEWLINE_CR) {
                this.backward(1);
            } else {
                nlLen += 1;
            }
        }

        return new ASCIILine(line, nlLen);
    }

    private static boolean contains(final byte v, final byte[] list) {
        for (byte item : list) {
            if (item == v) {
                return true;
            }
        }
        return false;
    }

    @Override
    public byte[] readBinary() {
        final int start = this.posIn.getPos();
        final int stop = this.consumeUntil(b -> b == 0);
        return (stop > start) ? Arrays.copyOfRange(this.getBuf(), start, stop) : EMPTY_BYTE_ARRAY;
    }

    /// Move forward until a byte matching `isEnd` is found, or the end
    /// of the buffer is reached. The matching byte is consumed.
    ///
    /// @param isEnd Condition of the end byte
    /// @return Buffer index of the matching byte, or the buffer length if not
    /// found
    private int consumeUntil(final IntPredicate isEnd) {
        final byte[] buf = this.getBuf();
        int i = this.posIn.getPos();
        while (i < buf.length && !isEnd.test(buf[i])) {
            i++;
        }
        this.posIn.setPos(Math.min(i + 1, buf.length));
        return i;
    }

    /// Set the current position back for `i` positions.
    @Override
    public int backward(final int i) {
        final int result = Math.max(this.posIn.getPos() - i, 0);
        this.posIn.setPos(result);
        return result;
    }

    /// Backward current position until the byte value `b`.
    ///
    /// @return the new position, or -1 if `b` not found
    @Override
    public int backwardTo(final byte b) {
        final byte[] buf = this.getBuf();
        for (int i = Math.min(this.posIn.getPos(), buf.length - 1); i > -1; i--) {
            if (buf[i] == b) {
                this.posIn.setPos(i);
                return i;
            }
        }
        return -1;
    }

    /// Forward current position until the byte value `b`.
    ///
    /// @param b Target byte
    /// @return the new position, or -1 if `b` not found
    public int forwardTo(final byte b) {
        final byte[] buf = this.getBuf();
        for (int i = this.posIn.getPos(); i < buf.length; i++) {
            if (buf[i] == b) {
                this.posIn.setPos(i);
                return i;
            }
        }
        return -1;
    }

    /// Backward current position until the byte array value `b`.
    ///
    /// @return the new position, or -1 if `b` not found
    @Override
    public int backwardTo(final byte[] b) {
        if ((b == null) || (b.length == 0)) {
            throw new IllegalArgumentException("Parameter b is null or empty.");
        }

        final byte[] buf = this.getBuf();
        for (int i = this.posIn.getPos() - b.length; i > -1; i--) {
            if (BytesTool.isByteArraySame(b, buf, i)) {
                this.posIn.setPos(i);
                return i;
            }
        }
        return -1;
    }

    /// Go to the end of the stream.
    @Override
    public void skipToEnd() {
        this.posIn.setPos(this.getBuf().length);
    }

    /// Fly to the specific `position`.
    @Override
    public void flyTo(final int position) {
        this.posIn.setPos(position);
    }

    /// Whether current location is the end or not.
    ///
    /// @return true Still has next byte to be read; false Current location is
    /// the end
    public boolean hasNext() {
        return this.posIn.getPos() < this.getBuf().length;
    }

    /// An ASCII line read by {@link #readASCIILine()}.
    ///
    /// @param line Line text, excluding the {@link PosDataInputStream#NEWLINE_CR} /
    /// {@link PosDataInputStream#NEWLINE_LF}
    /// @param newLineLength New Line length, could be 1 (0x0D) or 2 (0x0D0A), or 0
    public record ASCIILine(String line, int newLineLength) {

        /// Length of the line, including the {@link PosDataInputStream#NEWLINE_CR} /
        /// {@link PosDataInputStream#NEWLINE_LF}.
        ///
        /// @return {@link ASCIILine} length
        public int length() {
            return this.line.length() + this.newLineLength;
        }

        @Override
        public String toString() {
            return this.line;
        }
    }
}
