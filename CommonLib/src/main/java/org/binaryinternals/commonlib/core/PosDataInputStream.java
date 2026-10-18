/*
 * PosDataInputStream.java    August 8, 2007, 12:48 PM
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.commonlib.core;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Amos Shi
 */
public class PosDataInputStream extends DataInputStream implements DataInputEx {

    public static final int USHORT_LENGTH = 2;
    public static final byte[] EMPTY_BYTE_ARRAY = {};

    /**
     * Number in bytes for Java <code>byte</code> type.
     */
    public static final int LENGTH_BYTE = 1;
    /**
     * Number in bytes for Java <code>short</code> type.
     */
    public static final int LENGTH_SHORT = 2;
    /**
     * Number in bytes for Java <code>int</code> type.
     */
    public static final int LENGTH_INT = 4;
    /**
     * Number in bytes for Java <code>long</code> type.
     */
    public static final int LENGTH_LONG = 8;

    /**
     * Shift Operators, offset with 8.
     */
    protected static final int SHIFT_8 = 8;
    /**
     * Shift Operators, offset with 16.
     */
    protected static final int SHIFT_16 = 16;
    /**
     * Shift Operators, offset with 24.
     */
    protected static final int SHIFT_24 = 24;
    /**
     * Shift Operators, offset with 32.
     */
    protected static final int SHIFT_32 = 32;
    /**
     * Shift Operators, offset with 40.
     */
    protected static final int SHIFT_40 = 40;
    /**
     * Shift Operators, offset with 48.
     */
    protected static final int SHIFT_48 = 48;
    /**
     * Shift Operators, offset with 56.
     */
    protected static final int SHIFT_56 = 56;
    /**
     * Half Byte length: 4.
     */
    protected static final int BYTE_LENGTH_4 = 4;
    /**
     * Full Byte length: 8.
     */
    protected static final int BYTE_LENGTH_8 = 8;

    /** Byte offset 0. */
    protected static final int BYTE_OFFSET_0 = 0;
    /** Byte offset 1. */
    protected static final int BYTE_OFFSET_1 = 1;
    /** Byte offset 2. */
    protected static final int BYTE_OFFSET_2 = 2;
    /** Byte offset 3. */
    protected static final int BYTE_OFFSET_3 = 3;
    /** Byte offset 4. */
    protected static final int BYTE_OFFSET_4 = 4;
    /** Byte offset 5. */
    protected static final int BYTE_OFFSET_5 = 5;
    /** Byte offset 6. */
    protected static final int BYTE_OFFSET_6 = 6;
    /** Byte offset 7. */
    protected static final int BYTE_OFFSET_7 = 7;
    /**
     * Byte max value: 255.
     */
    protected static final int BYTE_MAX_255 = 255;

    /**
     * New line character: LINE FEED (LF).
     */
    public static final byte NEWLINE_LF = 0x0A;
    /**
     * New line character: CARRIAGE RETURN (CR).
     */
    public static final byte NEWLINE_CR = 0x0D;

    /**
     * Offset of the 1st byte.
     */
    protected int offset = 0;

    /**
     * Creates a new instance of PosDataInputStream.
     *
     * @param in Binary data input stream
     */
    public PosDataInputStream(final PosByteArrayInputStream in) {
        super(in);
    }

    /**
     * Create a sub {@link PosDataInputStream}, which starts from
     * <code>offset</code>.
     *
     * @param in Binary data input stream
     * @param offset Offset of the stream
     */
    public PosDataInputStream(final PosByteArrayInputStream in, int offset) {
        super(in);
        this.offset = offset;
    }

    /**
     * Get a partial {@link PosDataInputStream}, which starts from
     * <code>startPos</code> of original stream, with length
     * <code>length</code>.
     *
     * @param startPos Start position
     * @param length Length
     * @return A partial {@link PosDataInputStream} object
     */
    public PosDataInputStream getPartialStream(final int startPos, final int length) {
        return new PosDataInputStream(
                new PosByteArrayInputStream(this.getBuf(startPos, length)),
                startPos);
    }

    /**
     * Get the absolute position of the starting point of the buffer.
     *
     * @return buffer absolute position
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * Get current absolute position of the file.
     *
     * @return The index of the next character to read from the input stream
     * buffer, or <code>-1</code> if there is internal error, the input stream
     * is not <code>PosByteArrayInputStream</code>.
     */
    public int getPos() {
        if (this.in instanceof PosByteArrayInputStream posIn) {
            return posIn.getPos() + this.offset;
        }

        return -1;
    }

    /**
     * Get the byte array buffer of the input stream.
     *
     * @return the byte array
     */
    public byte[] getBuf() {
        if (this.in instanceof PosByteArrayInputStream posIn) {
            return posIn.getBuf();
        } else {
            throw new UnsupportedOperationException("This method is called in incorrect context");
        }
    }

    /**
     * Return a part of the byte array.
     *
     * @param startPos Start Position of the original byte array
     * @param length Length of data to be read
     * @return the partial byte array
     */
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

    /**
     * @see
     * <a href="http://technologicaloddity.com/2010/09/22/biginteger-as-unsigned-long-in-java/">
     * BigInteger as unsigned long in Java</a>
     */
    @Override
    public BigInteger readUnsignedLong() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];
        super.readFully(readBuffer, 0, BYTE_LENGTH_8);
        return new BigInteger(1, readBuffer);
    }

    @Override
    public BigInteger readUnsignedLongInLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];
        final byte[] readBufferLE = new byte[BYTE_LENGTH_8];
        super.readFully(readBuffer, 0, BYTE_LENGTH_8);
        for (int i = 0; i < BYTE_LENGTH_8; i++) {
            readBufferLE[i] = readBuffer[BYTE_LENGTH_8 - 1 - i];
        }

        return new BigInteger(1, readBufferLE);
    }

    @Override
    public String readASCII(final int length) throws IOException {
        if (length <= 0) {
            throw new IllegalArgumentException(
                    String.format("Parameter length should be greater than 0. (length = %d)", length));
        }

        final byte[] bytes = new byte[length];
        this.readFully(bytes);
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    @Override
    public String readASCII() throws IOException {
        return this.readASCIIUntil((byte) 0);
    }

    /**
     * Read current byte array as ASCII string until <code>byte</code>
     * <code>end</code>.
     *
     * <pre>
     * java:S135 - Loops should not contain more than a single "break" or "continue" statement --- We need it in this method
     * </pre>
     *
     * @param end End character
     * @throws IOException Read failed
     */
    @Override
    @SuppressWarnings("java:S135")
    public String readASCIIUntil(final byte end) throws IOException {
        byte b;
        StringBuilder sb = new StringBuilder();

        do {
            try {
                b = this.readByte();
                if (b == end) {
                    break;
                }
                sb.append((char) (b & BYTE_MAX_255));
            } catch (EOFException eof) {
                break;
            }
        } while (true);

        return sb.toString();
    }

    /**
     * Read current byte array as ASCII string until any <code>byte</code> in
     * array <code>end</code>.
     *
     * @param end End value for the ASCII string
     * @return ASCII as string
     * @throws IOException Read failed
     *
     * <pre>
     * java:S135 - Loops should not contain more than a single "break" or "continue" statement --- We need it in this method
     * </pre>
     */
    @SuppressWarnings("java:S135")
    public String readASCIIUntil(byte... end) throws IOException {
        if (end == null || end.length < 1) {
            throw new IllegalArgumentException("Inalid parameter 'end'.");
        }

        byte b;
        StringBuilder sb = new StringBuilder(100);

        do {
            try {
                b = this.readByte();
                if (this.contains(b, end)) {
                    break;
                }
                sb.append((char) (b & BYTE_MAX_255));
            } catch (EOFException eof) {
                break;
            }
        } while (true);

        return sb.toString();
    }

    /**
     * Read current byte array as ASCII string until a {@link #NEWLINE_CR} /
     * {@link #NEWLINE_LF} flag found.
     *
     * @return ASCII Line
     * @throws IOException Read failed
     */
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

    private boolean contains(final byte v, final byte[] list) {
        for (byte item : list) {
            if (item == v) {
                return true;
            }
        }
        return false;
    }

    /**
     * <pre>
     * java:S135 - Loops should not contain more than a single "break" or "continue" statement --- We need it in this method
     * </pre>
     */
    @Override
    @SuppressWarnings("java:S135")
    public byte[] readBinary() throws IOException {
        int size = this.getBuf().length - this.getPos() + this.offset + 1;
        byte[] big = new byte[size];
        int bigCounter = 0;
        byte b;

        do {
            try {
                b = this.readByte();
                if (b == 0) {
                    break;
                }
                big[bigCounter] = b;
                bigCounter++;
            } catch (EOFException eof) {
                break;
            }
        } while (true);

        if (bigCounter > 0) {
            return Arrays.copyOf(big, bigCounter);
        } else {
            return EMPTY_BYTE_ARRAY;
        }
    }

    /**
     * Set the current position back for <code>i</code> positions.
     *
     * This method supports {@link PosByteArrayInputStream} only, nothing will
     * do for other input stream types.
     *
     * @see PosByteArrayInputStream
     */
    @Override
    public int backward(final int i) {
        int result = -1;

        if (this.in instanceof PosByteArrayInputStream posIn) {
            result = Math.max(posIn.getPos() - i, 0);
            posIn.setPos(result);
        }

        return result;
    }

    /**
     * Backward current position until the byte value <code>b</code>.
     *
     * This method supports {@link PosByteArrayInputStream} only as input stream
     * only, otherwise <code>-1</code> is returned.
     *
     * @see PosByteArrayInputStream
     * @return the new position, or -1 if <code>b</code> not found
     */
    @Override
    public int backwardTo(final byte b) {
        int result = -1;

        if (this.in instanceof PosByteArrayInputStream posIn) {
            byte[] buf = posIn.getBuf();
            for (int i = Math.min(posIn.getPos(), buf.length - 1); i > -1; i--) {
                if (buf[i] == b) {
                    result = i;
                    break;
                }
            }

            if (result != -1) {
                posIn.setPos(result);
            }
        }

        return result;
    }

    /**
     * Forward current position until the byte value <code>b</code>.
     *
     * This method supports {@link PosByteArrayInputStream} only as input stream
     * only, otherwise <code>-1</code> is returned.
     *
     * @param b Target byte
     * @see PosByteArrayInputStream
     * @return the new position, or -1 if <code>b</code> not found
     */
    public int forwardTo(final byte b) {
        int result = -1;

        if (this.in instanceof PosByteArrayInputStream posIn) {
            byte[] buf = posIn.getBuf();
            for (int i = posIn.getPos(); i < buf.length; i++) {
                if (buf[i] == b) {
                    result = i;
                    break;
                }
            }

            if (result != -1) {
                posIn.setPos(result);
            }
        }

        return result;
    }

    /**
     * Backward current position until the byte array value <code>b</code>.
     *
     * This method supports {@link PosByteArrayInputStream} only as input stream
     * only, otherwise <code>-1</code> is returned.
     *
     * @see PosByteArrayInputStream
     */
    @Override
    public int backwardTo(final byte[] b) {
        int result = -1;

        if ((b == null) || (b.length == 0)) {
            throw new IllegalArgumentException("Parameter b is null or empty.");
        }

        if (this.in instanceof PosByteArrayInputStream posIn) {
            byte[] buf = posIn.getBuf();
            for (int i = posIn.getPos() - b.length; i > -1; i--) {
                if (BytesTool.isByteArraySame(b, buf, i)) {
                    result = i;
                    break;
                }
            }

            if (result != -1) {
                posIn.setPos(result);
            }
        }

        return result;
    }

    /**
     * Go to the end of the stream.
     *
     * This method supports {@link PosByteArrayInputStream} only, nothing will
     * do for other input stream types.
     *
     * @see PosByteArrayInputStream
     */
    @Override
    public void skipToEnd() throws IOException {
        if (this.in instanceof PosByteArrayInputStream posIn) {
            posIn.setPos(0);
            posIn.skip(posIn.getBuf().length);
        }
    }

    /**
     * Fly to the specific <code>position</code>.
     *
     * This method supports {@link PosByteArrayInputStream} only, nothing will
     * do for other input stream types.
     *
     * @see PosByteArrayInputStream
     */
    @Override
    public void flyTo(final int position) {
        if (this.in instanceof PosByteArrayInputStream posIn) {
            posIn.setPos(position);
        }
    }

    /**
     * Whether current location is the end or not.
     *
     * @return true Still has next byte to be read; false Current location is
     * the end
     */
    public boolean hasNext() {
        return this.getPos() - this.offset <= (this.getBuf().length - 1);
    }

    public static class ASCIILine {

        /**
         * New Line length, could be 1 (0x0D) or 2 (0x0D0A), or 0.
         */
        public final int newLineLength;
        public final String line;

        public ASCIILine(String line, int nlLen) {
            this.line = line;
            this.newLineLength = nlLen;
        }

        /**
         * Length of the line, including the {@link #NEWLINE_CR} /
         * {@link #NEWLINE_LF}.
         *
         * @return {@link ASCIILine} length
         */
        public int length() {
            return this.line.length() + newLineLength;
        }

        @Override
        public String toString() {
            return this.line;
        }
    }
}
