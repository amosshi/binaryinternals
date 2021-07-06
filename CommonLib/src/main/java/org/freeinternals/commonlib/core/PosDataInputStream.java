/*
 * PosDataInputStream.java    August 8, 2007, 12:48 PM
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.core;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;

/**
 *
 * @author Amos Shi
 */
public class PosDataInputStream extends DataInputStream implements DataInputEx {

    /**
     * Shift Operators, offset with 8.
     */
    private static final int SHIFT_8 = 8;
    /**
     * Shift Operators, offset with 16.
     */
    private static final int SHIFT_16 = 16;
    /**
     * Shift Operators, offset with 24.
     */
    private static final int SHIFT_24 = 24;
    /**
     * Shift Operators, offset with 32.
     */
    private static final int SHIFT_32 = 32;
    /**
     * Shift Operators, offset with 40.
     */
    private static final int SHIFT_40 = 40;
    /**
     * Shift Operators, offset with 48.
     */
    private static final int SHIFT_48 = 48;
    /**
     * Shift Operators, offset with 56.
     */
    private static final int SHIFT_56 = 56;
    /**
     * Half Byte length: 4.
     */
    private static final int BYTE_LENGTH_4 = 4;
    /**
     * Full Byte length: 8.
     */
    private static final int BYTE_LENGTH_8 = 8;

    /** Byte offset 0. */
    private static final int BYTE_OFFSET_0 = 0;
    /** Byte offset 1. */
    private static final int BYTE_OFFSET_1 = 1;
    /** Byte offset 2. */
    private static final int BYTE_OFFSET_2 = 2;
    /** Byte offset 3. */
    private static final int BYTE_OFFSET_3 = 3;
    /** Byte offset 4. */
    private static final int BYTE_OFFSET_4 = 4;
    /** Byte offset 5. */
    private static final int BYTE_OFFSET_5 = 5;
    /** Byte offset 6. */
    private static final int BYTE_OFFSET_6 = 6;
    /** Byte offset 7. */
    private static final int BYTE_OFFSET_7 = 7;
    /**
     * Byte max value: 255.
     */
    private static final int BYTE_MAX_255 = 255;

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
        int pos = -1;
        if (this.in instanceof PosByteArrayInputStream) {
            pos = ((PosByteArrayInputStream) this.in).getPos() + this.offset;
        }

        return pos;
    }

    /**
     * Get the byte array buffer of the input stream.
     *
     * @return the byte array
     */
    public byte[] getBuf() {
        if (this.in instanceof PosByteArrayInputStream) {
            return ((PosByteArrayInputStream) this.in).getBuf();
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

        byte[] bufFull = this.getBuf();
        if (startPos + length - 1 > bufFull.length) {
            throw new ArrayIndexOutOfBoundsException("The last item index is bigger than class byte array size.");
        }

        final byte[] bufPart = new byte[length];
        System.arraycopy(bufFull, startPos, bufPart, 0, length);
        return bufPart;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface Methods
    @Override
    public short readShortInLittleEndian() throws IOException {
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch2 << SHIFT_8) + (ch1));
    }

    @Override
    public int readUnsignedShortInLittleEndian() throws IOException {
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch2 << SHIFT_8) + (ch1);
    }

    @Override
    @SuppressWarnings("java:S1110") // Redundant parenthesis is needed for readability
    public int readIntInLittleEndian() throws IOException {
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        int ch3 = this.in.read();
        int ch4 = this.in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (((ch4 << SHIFT_24) + (ch3 << SHIFT_16) + (ch2 << SHIFT_8) + (ch1)));
    }

    @Override
    @SuppressWarnings("java:S1110") // Redundant parenthesis is needed for readability
    public long readUnsignedInt() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];

        super.readFully(readBuffer, BYTE_LENGTH_4, BYTE_LENGTH_4);
        readBuffer[BYTE_OFFSET_0] = 0;
        readBuffer[BYTE_OFFSET_1] = 0;
        readBuffer[BYTE_OFFSET_2] = 0;
        readBuffer[BYTE_OFFSET_3] = 0;

        return (((long) readBuffer[BYTE_OFFSET_0] << SHIFT_56)
                + ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_48)
                + ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_40)
                + ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_32)
                + ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_24)
                + ((readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_16)
                + ((readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_8)
                + ((readBuffer[BYTE_OFFSET_7] & BYTE_MAX_255)));
    }

    @Override
    @SuppressWarnings("java:S1110") // Redundant parenthesis is needed for readability
    public long readUnsignedIntInLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];

        super.readFully(readBuffer, 0, BYTE_LENGTH_4);
        readBuffer[BYTE_OFFSET_7] = readBuffer[BYTE_OFFSET_0];
        readBuffer[BYTE_OFFSET_6] = readBuffer[BYTE_OFFSET_1];
        readBuffer[BYTE_OFFSET_5] = readBuffer[BYTE_OFFSET_2];
        readBuffer[BYTE_OFFSET_4] = readBuffer[BYTE_OFFSET_3];
        readBuffer[BYTE_OFFSET_0] = 0;
        readBuffer[BYTE_OFFSET_1] = 0;
        readBuffer[BYTE_OFFSET_2] = 0;
        readBuffer[BYTE_OFFSET_3] = 0;

        return (((long) readBuffer[BYTE_OFFSET_0] << SHIFT_56)
                + ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_48)
                + ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_40)
                + ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_32)
                + ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_24)
                + ((readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_16)
                + ((readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_8)
                + ((readBuffer[BYTE_OFFSET_7] & BYTE_MAX_255)));
    }

    @Override
    @SuppressWarnings("java:S1110") // Redundant parenthesis is needed for readability
    public long readLongInLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];
        super.readFully(readBuffer, 0, 8);
        return (((long) readBuffer[BYTE_OFFSET_7] << SHIFT_56)
                + ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_48)
                + ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                + ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                + ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                + ((readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                + ((readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                + ((readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255)));
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

        StringBuilder sb = new StringBuilder(length + 1);
        for (int i = 0; i < length; i++) {
            sb.append((char) this.readByte());
        }
        return sb.toString();
    }

    @Override
    public String readASCII() throws IOException {
        return this.readASCIIUntil((byte) 0);
    }

    /**
     * Read current byte array as ASCII string until <code>byte</code>
     * <code>end</code>.
     *
     * @param end
     * @throws java.io.IOException
     */
    @Override
    @SuppressWarnings("java:S135") // Loops should not contain more than a single "break" or "continue" statement
    public String readASCIIUntil(final byte end) throws IOException {
        byte b;
        StringBuilder sb = new StringBuilder();

        do {
            try {
                b = this.readByte();
                if (b == end) {
                    break;
                }
                sb.append((char) b);
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
     * @throws java.io.IOException Read failed
     */
    @SuppressWarnings("java:S135") // Loops should not contain more than a single "break" or "continue" statement
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
                sb.append((char) b);
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
     * @return
     * @throws java.io.IOException
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
        boolean result = false;
        for (int i = 0; i < list.length; i++) {
            if (list[i] == v) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    @SuppressWarnings("java:S135") // Loops should not contain more than a single "break" or "continue" statement
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
            byte[] result = new byte[bigCounter];
            System.arraycopy(big, 0, result, 0, bigCounter);
            return result;
        } else {
            return null;
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

        if (this.in instanceof PosByteArrayInputStream) {
            PosByteArrayInputStream posIn = ((PosByteArrayInputStream) this.in);
            int currentPos = posIn.getPos();
            result = ((currentPos - i) > 0) ? (currentPos - i) : 0;
            ((PosByteArrayInputStream) this.in).setPos(result);
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

        if (this.in instanceof PosByteArrayInputStream) {
            PosByteArrayInputStream posIn = ((PosByteArrayInputStream) this.in);
            byte[] buf = posIn.getBuf();
            for (int i = posIn.getPos(); i > -1; i--) {
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
     * @param b
     * @see PosByteArrayInputStream
     * @return the new position, or -1 if <code>b</code> not found
     */
    public int forwardTo(final byte b) {
        int result = -1;

        if (this.in instanceof PosByteArrayInputStream) {
            PosByteArrayInputStream posIn = ((PosByteArrayInputStream) this.in);
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

        if (this.in instanceof PosByteArrayInputStream) {
            PosByteArrayInputStream posIn = ((PosByteArrayInputStream) this.in);
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
     * This method supports {@link PosByteArrayInputStream} only, nothing will
     * do for other input stream types.
     *
     * @see PosByteArrayInputStream
     */
    @Override
    public void skipToEnd() throws IOException {
        if (this.in instanceof PosByteArrayInputStream) {
            PosByteArrayInputStream posIn = ((PosByteArrayInputStream) this.in);
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
        if (this.in instanceof PosByteArrayInputStream) {
            ((PosByteArrayInputStream) this.in).setPos(position);
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
