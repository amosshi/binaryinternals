/*
 * PosDataInputStreamDex.java    June 17, 2015, 21:29
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.io.EOFException;
import java.io.IOException;
import org.binaryinternals.commonlib.core.PosByteArrayInputStream;
import org.binaryinternals.commonlib.core.PosDataInputStream;
import org.binaryinternals.commonlib.core.FileFormatException;
import org.binaryinternals.format.dex.header_item.Endian;

/**
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We use different naming convention for better readablity
 * java:S1110 - Redundant parenthesis --- Redundant parenthesis is needed for readability
 * </pre>
 */
@SuppressWarnings({"java:S100", "java:S1110"})
public class PosDataInputStreamDex extends PosDataInputStream {

    /**
     * Full Byte length: 3.
     */
    private static final int BYTE_LENGTH_3 = 3;
    private static final int BYTE_LENGTH_5 = 5;
    private static final int BYTE_LENGTH_6 = 6;
    private static final int BYTE_LENGTH_7 = 7;

    /**
     * Byte position: 6.
     */
    private static final int BYTE_POSITION_5 = 5;

    /**
     * Endian of the {@link DexFile}. The default value is little-endian
     * {@link header_item.Endian#ENDIAN_CONSTANT}, as the DEX format
     * specification said.
     */
    protected final header_item.Endian endian;

    public PosDataInputStreamDex(PosByteArrayInputStream in) {
        super(in);
        this.endian = Endian.ENDIAN_CONSTANT;
    }

    public PosDataInputStreamDex(PosByteArrayInputStream in, header_item.Endian e) {
        super(in);
        this.endian = e;
    }

    /**
     * Read a {@link Type_byte} from the input stream.
     *
     * @return a {@link Type_byte}
     * @throws IOException I/O error
     */
    public Type_byte Dex_byte() throws IOException {
        return new Type_byte(this.readByte());
    }

    /**
     * Read a {@link Type_ubyte} from the input stream.
     *
     * @return a {@link Type_ubyte}
     * @throws IOException I/O error
     */
    public Type_ubyte Dex_ubyte() throws IOException {
        return new Type_ubyte(this.readUnsignedByte());
    }

    /**
     * Read a {@link Type_short} from the input stream.
     *
     * @return a {@link Type_short}
     * @throws IOException I/O Error
     */
    public Type_short Dex_short() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_short(this.readShort());
        } else {
            return new Type_short(this.readShortInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_ushort} from the input stream.
     *
     * @return a {@link Type_ushort}
     * @throws IOException I/O Error
     */
    public Type_ushort Dex_ushort() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_ushort(this.readUnsignedShort());
        } else {
            return new Type_ushort(this.readUnsignedShortInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_int} from the input stream.
     *
     * @return a {@link Type_int}
     * @throws IOException I/O Error
     */
    public Type_int Dex_int() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_int(this.readInt());
        } else {
            return new Type_int(this.readIntInLittleEndian());
        }
    }

    /**
     * Read a 3-byte {@link Type_int} from the input stream.
     *
     * @return a {@link Type_int}
     * @throws IOException I/O Error
     */
    public Type_int Dex_int3() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_int(this.readInt3());
        } else {
            return new Type_int(this.readInt3InLittleEndian());
        }
    }

    /**
     * Read a {@link Type_uint} from the input stream.
     *
     * @return a {@link Type_uint}
     * @throws IOException I/O Error
     */
    public Type_uint Dex_uint() throws IOException {
        if (this.endian.value == header_item.Endian.ENDIAN_CONSTANT.value) {
            return new Type_uint(this.readUnsignedInt());
        } else {
            return new Type_uint(this.readUnsignedIntInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_uint} from the input stream for only 3 bytes.
     *
     * @return a {@link Type_uint}
     * @throws IOException I/O Error
     */
    public Type_uint Dex_uint3() throws IOException {
        if (this.endian.value == header_item.Endian.ENDIAN_CONSTANT.value) {
            return new Type_uint(this.readUnsignedInt3());
        } else {
            return new Type_uint(this.readUnsignedInt3InLittleEndian());
        }
    }

    /**
     * Read a {@link Type_long} from the input stream.
     *
     * @return a {@link Type_long}
     * @throws IOException I/O Error
     */
    public Type_long Dex_long() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_long(this.readLong());
        } else {
            return new Type_long(this.readLongInLittleEndian());
        }
    }

    /**
     * Read a 5/6/7-byte {@link Type_long} from the input stream.
     *
     * @param length Dynamic long length value: 5, 6, or 7
     * @return a {@link Type_long}
     * @throws IOException I/O Error
     */
    public Type_long Dex_long(int length) throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            switch (length) {
                case 5:
                    return new Type_long(this.readLong5());
                case 6:
                    return new Type_long(this.readLong6());
                case 7:
                    return new Type_long(this.readLong7());
                default:
            }
        } else {
            switch (length) {
                case 5:
                    return new Type_long(this.readLong5InLittleEndian());
                case 6:
                    return new Type_long(this.readLong6InLittleEndian());
                case 7:
                    return new Type_long(this.readLong7InLittleEndian());
                default:
            }
        }

        throw new IllegalArgumentException(String.format("Unexpected long value length: %d", length));
    }

    /**
     * Read a {@link Type_ulong} from the input stream.
     *
     * @return a {@link Type_ulong}
     * @throws IOException I/O Error
     */
    public Type_ulong Dex_ulong() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_ulong(this.readUnsignedLong());
        } else {
            return new Type_ulong(this.readUnsignedLongInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_sleb128} from the input stream.
     *
     * @throws IOException I/O Error
     * @throws FileFormatException Invalid LEB128 format
     * @return a {@link Type_sleb128}
     */
    public Type_sleb128 Dex_sleb128() throws IOException, FileFormatException {
        int startPos = super.getPos();
        int result = 0;
        int cur;
        int count = 0;
        int signBits = -1;

        do {
            cur = super.readByte() & 0xff;
            result |= (cur & 0x7f) << (count * 7);
            signBits <<= 7;
            count++;
        } while (((cur & 0x80) == 0x80) && count < 5);

        if ((cur & 0x80) == 0x80) {
            throw new FileFormatException("Invalid LEB128 sequence at file position " + super.getPos());
        }

        if (((signBits >> 1) & result) != 0) {
            result |= signBits;
        }

        return new Type_sleb128(result, super.getPos() - startPos);
    }

    /**
     * Read a {@link Type_uleb128} from the input stream.
     *
     * @throws IOException I/O Error
     * @throws FileFormatException Invalid LEB128 format
     * @return a {@link Type_uleb128}
     */
    public Type_uleb128 Dex_uleb128() throws IOException, FileFormatException {
        int startPos = super.getPos();
        int result = 0;
        int cur;
        int count = 0;

        do {
            cur = super.readByte() & 0xff;
            result |= (cur & 0x7f) << (count * 7);
            count++;
        } while (((cur & 0x80) == 0x80) && count < 5);

        if ((cur & 0x80) == 0x80) {
            throw new FileFormatException("Invalid LEB128 sequence at file position " + super.getPos());
        }

        return new Type_uleb128(result, super.getPos() - startPos);
    }

    /**
     * Read a {@link Type_uleb128p1} from the input stream.
     *
     * @throws IOException I/O Error
     * @throws FileFormatException Invalid LEB128 format
     * @return a {@link Type_uleb128p1}
     */
    public Type_uleb128p1 Dex_uleb128p1() throws IOException, FileFormatException {
        Type_uleb128 uleb128 = this.Dex_uleb128();
        return new Type_uleb128p1(uleb128.value - 1, uleb128.length);
    }

    public Double readDouble(int length) throws IOException{
        System.out.println(this.getClass().getSimpleName() + " VALUE_DOUBLE value_arg " + (length - 1) + " at 0x" + Integer.toHexString(this.getPos())  + " - to implment");

        byte[] raw = new byte[length];
        int rb = this.read(raw);
        if (rb != length) {
            throw new IOException(String.format("Cannot read enough bytes for double. expected=%d readbytes=%d", length, rb));
        }

        return Double.MAX_VALUE;
    }

    public Float readFloat(int length) throws IOException{
        System.out.println(this.getClass().getSimpleName() + " VALUE_FLOAT value_arg " + (length -1) + " at 0x" + Integer.toHexString(this.getPos())  + " - to implment");

        byte[] raw = new byte[length];
        int rb = this.read(raw);
        if (rb != length) {
            throw new IOException(String.format("Cannot read enough bytes for float. expected=%d readbytes=%d", length, rb));
        }

        return Float.MIN_VALUE;
    }

    /**
     * Read 3-byte int.
     */
    private int readInt3() throws IOException {
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        int ch3 = this.in.read();
        if ((ch1 | ch2 | ch3) < 0) {
            throw new EOFException();
        }

        if ((ch3 & 0x80) > 0) {
            System.out.println("TODO verify - 3-byte int test case at 0x" + Integer.toHexString(this.getPos()) + " ----------------------- readInt3 ----");
            return 0xFF000000 | (ch1 << SHIFT_16) | (ch2 << SHIFT_8) | (ch3);
        } else {
            return (ch1 << SHIFT_16) | (ch2 << SHIFT_8) | (ch3);
        }
    }

    /**
     * Read 3-byte int in little-endian.
     */
    private int readInt3InLittleEndian() throws IOException {
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        int ch3 = this.in.read();
        if ((ch1 | ch2 | ch3) < 0) {
            throw new EOFException();
        }

        if ((ch3 & 0x80) > 0) {
            // System.out.println("TODO verify via Java source code via minus value - 3-byte int test case at 0x" + Integer.toHexString(this.getPos()) + " ----------------------- readIntInLittleEndian3 ----");
            return 0xFF000000 | (ch3 << SHIFT_16) | (ch2 << SHIFT_8) | (ch1);
        } else {
            return (ch3 << SHIFT_16) | (ch2 << SHIFT_8) | (ch1);
        }
    }

    private long readLong5() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_5];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_4] & 0x80) > 0) {
            System.out.println("TODO test case at 0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong5 NEGATIVE----");
            return (0xFFFFFF0000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255)));
        } else {
            System.out.println("TODO test case at  0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong5 POSITIVE----");
            return (  ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255)));
        }
    }

    private long readLong5InLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_5];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_4] & 0x80) > 0) {
            System.out.println("TODO test case at   0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong5InLittleEndian NEGATIVE----");
            return (0xFFFFFF0000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255)));
        } else {
            return (  ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255)));
        }
    }

    private long readLong6() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_6];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_5] & 0x80) > 0) {
            System.out.println("TODO test case at 0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong6 NEGATIVE----");
            return (0xFFFF000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255)));
        } else {
            System.out.println("TODO test case at 0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong6 POSITIVE----");
            return (  ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255)));
        }
    }
    private long readLong6InLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_6];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_5] & 0x80) > 0) {
            System.out.println("TODO test case at 0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong6InLittleEndian NEGATIVE----");
            return (0xFFFF000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255)));
        } else {
            return (  ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255)));
        }
    }

    private long readLong7() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_7];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_6] & 0x80) > 0) {
            System.out.println("TODO test case at 0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong7 NEGATIVE----");
            return (0xFF00000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255)));
        } else {
            System.out.println("TODO test case at 0x" + Integer.toHexString(this.getPos()) + " ------------------- to verify ---- readLong7 POSITIVE----");
            return (  ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255)));
        }
    }
    private long readLong7InLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_7];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_6] & 0x80) > 0) {
            return (0xFF00000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255)));
        } else {
            return (  ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255)));
        }
    }

    /**
     * Read 3-byte unsigned int.
     */
    private long readUnsignedInt3() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];

        super.readFully(readBuffer, BYTE_POSITION_5, BYTE_LENGTH_3);
        readBuffer[BYTE_OFFSET_0] = 0;
        readBuffer[BYTE_OFFSET_1] = 0;
        readBuffer[BYTE_OFFSET_2] = 0;
        readBuffer[BYTE_OFFSET_3] = 0;
        readBuffer[BYTE_OFFSET_4] = 0;

        return (((long) readBuffer[BYTE_OFFSET_0] << SHIFT_56)
                + ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_48)
                + ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_40)
                + ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_32)
                + ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_24)
                + ((readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_16)
                + ((readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_8)
                + ((readBuffer[BYTE_OFFSET_7] & BYTE_MAX_255)));
    }

    /**
     * Read 3-byte unsigned int in little-endian.
     */
    private long readUnsignedInt3InLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];

        super.readFully(readBuffer, 0, BYTE_LENGTH_3);
        readBuffer[BYTE_OFFSET_7] = readBuffer[BYTE_OFFSET_0];
        readBuffer[BYTE_OFFSET_6] = readBuffer[BYTE_OFFSET_1];
        readBuffer[BYTE_OFFSET_5] = readBuffer[BYTE_OFFSET_2];
        readBuffer[BYTE_OFFSET_4] = 0;
        readBuffer[BYTE_OFFSET_3] = 0;
        readBuffer[BYTE_OFFSET_2] = 0;
        readBuffer[BYTE_OFFSET_1] = 0;
        readBuffer[BYTE_OFFSET_0] = 0;

        return (((long) readBuffer[BYTE_OFFSET_0] << SHIFT_56)
                + ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_48)
                + ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_40)
                + ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_32)
                + ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_24)
                + ((readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_16)
                + ((readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_8)
                + ((readBuffer[BYTE_OFFSET_7] & BYTE_MAX_255)));
    }
}
