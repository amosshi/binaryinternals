/*
 * PosDataInputStreamDex.java    June 17, 2015, 21:29
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.dex.header_item.Endian;

/// @author Amos Shi
///
/// ```
/// java:S100 - Method names should comply with a naming convention --- We use different naming convention for better readablity
/// java:S1110 - Redundant parenthesis --- Redundant parenthesis is needed for readability
/// ```
@SuppressWarnings({"java:S100", "java:S1110"})
public final class PosDataInputStreamDex extends PosDataInputStream {

    /// Full Byte length: 3.
    private static final int BYTE_LENGTH_3 = 3;
    private static final int BYTE_LENGTH_5 = 5;
    private static final int BYTE_LENGTH_6 = 6;
    private static final int BYTE_LENGTH_7 = 7;

    /// Byte position: 6.
    private static final int BYTE_POSITION_5 = 5;

    /// Endian of the [DexFile]. The default value is little-endian
    /// [header_item.Endian#ENDIAN_CONSTANT], as the DEX format
    /// specification said.
    private final header_item.Endian endian;

    private final ByteOrder byteOrder;

    public PosDataInputStreamDex(PosByteArrayInputStream in) {
        super(in);
        this.endian = Endian.ENDIAN_CONSTANT;
        this.byteOrder = this.endian.toByteOrder();
    }

    public PosDataInputStreamDex(PosByteArrayInputStream in, header_item.Endian e) {
        super(in);
        this.endian = e;
        this.byteOrder = e.toByteOrder();
    }

    /// Read a [Type_byte] from the input stream.
    ///
    /// @return a [Type_byte]
    /// @throws IOException I/O error
    public Type_byte Dex_byte() throws IOException {
        return new Type_byte(this.readByte());
    }

    /// Read a [Type_ubyte] from the input stream.
    ///
    /// @return a [Type_ubyte]
    /// @throws IOException I/O error
    public Type_ubyte Dex_ubyte() throws IOException {
        return new Type_ubyte(this.readUnsignedByte());
    }

    /// Read a [Type_short] from the input stream.
    ///
    /// @return a [Type_short]
    /// @throws IOException I/O Error
    public Type_short Dex_short() throws IOException {
        return new Type_short(this.readShort(this.byteOrder));
    }

    /// Read a [Type_ushort] from the input stream.
    ///
    /// @return a [Type_ushort]
    /// @throws IOException I/O Error
    public Type_ushort Dex_ushort() throws IOException {
        return new Type_ushort(this.readUnsignedShort(this.byteOrder));
    }

    /// Read a [Type_int] from the input stream.
    ///
    /// @return a [Type_int]
    /// @throws IOException I/O Error
    public Type_int Dex_int() throws IOException {
        return new Type_int(this.readInt(this.byteOrder));
    }

    /// Read a 3-byte [Type_int] from the input stream.
    ///
    /// @return a [Type_int]
    /// @throws IOException I/O Error
    public Type_int Dex_int3() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_int(this.readInt3());
        } else {
            return new Type_int(this.readInt3InLittleEndian());
        }
    }

    /// Read a [Type_uint] from the input stream.
    ///
    /// @return a [Type_uint]
    /// @throws IOException I/O Error
    public Type_uint Dex_uint() throws IOException {
        return new Type_uint(this.readUnsignedInt(this.byteOrder));
    }

    /// Read a [Type_uint] from the input stream for only 3 bytes.
    ///
    /// @return a [Type_uint]
    /// @throws IOException I/O Error
    public Type_uint Dex_uint3() throws IOException {
        if (this.endian == header_item.Endian.ENDIAN_CONSTANT) {
            return new Type_uint(this.readUnsignedInt3());
        } else {
            return new Type_uint(this.readUnsignedInt3InLittleEndian());
        }
    }

    /// Read a [Type_long] from the input stream.
    ///
    /// @return a [Type_long]
    /// @throws IOException I/O Error
    public Type_long Dex_long() throws IOException {
        return new Type_long(this.readLong(this.byteOrder));
    }

    /// Read a 5/6/7-byte [Type_long] from the input stream.
    ///
    /// @param length Dynamic long length value: 5, 6, or 7
    /// @return a [Type_long]
    /// @throws IOException I/O Error
    public Type_long Dex_long(int length) throws IOException {
        final boolean constant = this.endian == header_item.Endian.ENDIAN_CONSTANT;
        final long value = switch (length) {
            case 5 -> constant ? this.readLong5() : this.readLong5InLittleEndian();
            case 6 -> constant ? this.readLong6() : this.readLong6InLittleEndian();
            case 7 -> constant ? this.readLong7() : this.readLong7InLittleEndian();
            default -> throw new IllegalArgumentException("Unexpected long value length: %d".formatted(length));
        };
        return new Type_long(value);
    }

    /// Read a [Type_ulong] from the input stream.
    ///
    /// @return a [Type_ulong]
    /// @throws IOException I/O Error
    public Type_ulong Dex_ulong() throws IOException {
        return new Type_ulong(this.readUnsignedLong(this.byteOrder));
    }

    /// Read a [Type_sleb128] from the input stream.
    ///
    /// @throws IOException I/O Error
    /// @throws FileFormatException Invalid LEB128 format
    /// @return a [Type_sleb128]
    public Type_sleb128 Dex_sleb128() throws IOException {
        final int startPos = super.getPos();
        int result = 0;
        int cur;
        int count = 0;
        int signBits = -1;

        do {
            cur = super.readUnsignedByte();
            result |= (cur & 0x7F) << (count * 7);
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

    /// Read a [Type_uleb128] from the input stream.
    ///
    /// @throws IOException I/O Error
    /// @throws FileFormatException Invalid LEB128 format
    /// @return a [Type_uleb128]
    public Type_uleb128 Dex_uleb128() throws IOException {
        int startPos = super.getPos();
        int result = 0;
        int cur;
        int count = 0;

        do {
            cur = super.readUnsignedByte();
            result |= (cur & 0x7F) << (count * 7);
            count++;
        } while (((cur & 0x80) == 0x80) && count < 5);

        if ((cur & 0x80) == 0x80) {
            throw new FileFormatException("Invalid LEB128 sequence at file position " + super.getPos());
        }

        return new Type_uleb128(result, super.getPos() - startPos);
    }

    /// Read a [Type_uleb128p1] from the input stream.
    ///
    /// @throws IOException I/O Error
    /// @throws FileFormatException Invalid LEB128 format
    /// @return a [Type_uleb128p1]
    public Type_uleb128p1 Dex_uleb128p1() throws IOException {
        Type_uleb128 uleb128 = this.Dex_uleb128();
        return new Type_uleb128p1(uleb128.value - 1, uleb128.length);
    }

    /// Skip a `double` value.
    /// TODO Not implemented, the value is not read.
    ///
    /// @param length Length of the value in bytes
    /// @return [Double#MAX_VALUE] as a place holder
    /// @throws IOException I/O Error
    public double readDouble(int length) throws IOException {
        this.readFully(new byte[length]);
        return Double.MAX_VALUE;
    }

    /// Skip a `float` value.
    /// TODO Not implemented, the value is not read.
    ///
    /// @param length Length of the value in bytes
    /// @return [Float#MIN_VALUE] as a place holder
    /// @throws IOException I/O Error
    public float readFloat(int length) throws IOException {
        this.readFully(new byte[length]);
        return Float.MIN_VALUE;
    }

    /// Read 3-byte int.
    private int readInt3() throws IOException {
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        int ch3 = this.in.read();
        if ((ch1 | ch2 | ch3) < 0) {
            throw new EOFException();
        }

        if ((ch3 & 0x80) > 0) {
            return 0xFF000000 | (ch1 << SHIFT_16) | (ch2 << SHIFT_8) | (ch3);
        } else {
            return (ch1 << SHIFT_16) | (ch2 << SHIFT_8) | (ch3);
        }
    }

    /// Read 3-byte int in little-endian.
    private int readInt3InLittleEndian() throws IOException {
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        int ch3 = this.in.read();
        if ((ch1 | ch2 | ch3) < 0) {
            throw new EOFException();
        }

        if ((ch3 & 0x80) > 0) {
            return 0xFF000000 | (ch3 << SHIFT_16) | (ch2 << SHIFT_8) | (ch1);
        } else {
            return (ch3 << SHIFT_16) | (ch2 << SHIFT_8) | (ch1);
        }
    }

    private long readLong5() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_5];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_4] & 0x80) > 0) {
            return 0xFFFFFF0000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255));
        } else {
            return ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255));
        }
    }

    private long readLong5InLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_5];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_4] & 0x80) > 0) {
            return 0xFFFFFF0000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255));
        } else {
            return ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255));
        }
    }

    private long readLong6() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_6];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_5] & 0x80) > 0) {
            return 0xFFFF000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255));
        } else {
            return ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255));
        }
    }

    private long readLong6InLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_6];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_5] & 0x80) > 0) {
            return 0xFFFF000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255));
        } else {
            return ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255));
        }
    }

    private long readLong7() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_7];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_6] & 0x80) > 0) {
            return 0xFF00000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255));
        } else {
            return ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255));
        }
    }

    private long readLong7InLittleEndian() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_7];
        super.readFully(readBuffer);

        if ((readBuffer[BYTE_OFFSET_6] & 0x80) > 0) {
            return 0xFF00000000000000L
                    | ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255));
        } else {
            return ((long) (readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_48)
                    | ((long) (readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_40)
                    | ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_32)
                    | ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_24)
                    | ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_16)
                    | ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_8)
                    | ((long) (readBuffer[BYTE_OFFSET_0] & BYTE_MAX_255));
        }
    }

    /// Read 3-byte unsigned int.
    private long readUnsignedInt3() throws IOException {
        final byte[] readBuffer = new byte[BYTE_LENGTH_8];

        super.readFully(readBuffer, BYTE_POSITION_5, BYTE_LENGTH_3);
        readBuffer[BYTE_OFFSET_0] = 0;
        readBuffer[BYTE_OFFSET_1] = 0;
        readBuffer[BYTE_OFFSET_2] = 0;
        readBuffer[BYTE_OFFSET_3] = 0;
        readBuffer[BYTE_OFFSET_4] = 0;

        return ((long) readBuffer[BYTE_OFFSET_0] << SHIFT_56)
                + ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_48)
                + ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_40)
                + ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_32)
                + ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_24)
                + ((readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_16)
                + ((readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_8)
                + ((readBuffer[BYTE_OFFSET_7] & BYTE_MAX_255));
    }

    /// Read 3-byte unsigned int in little-endian.
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

        return ((long) readBuffer[BYTE_OFFSET_0] << SHIFT_56)
                + ((long) (readBuffer[BYTE_OFFSET_1] & BYTE_MAX_255) << SHIFT_48)
                + ((long) (readBuffer[BYTE_OFFSET_2] & BYTE_MAX_255) << SHIFT_40)
                + ((long) (readBuffer[BYTE_OFFSET_3] & BYTE_MAX_255) << SHIFT_32)
                + ((long) (readBuffer[BYTE_OFFSET_4] & BYTE_MAX_255) << SHIFT_24)
                + ((readBuffer[BYTE_OFFSET_5] & BYTE_MAX_255) << SHIFT_16)
                + ((readBuffer[BYTE_OFFSET_6] & BYTE_MAX_255) << SHIFT_8)
                + ((readBuffer[BYTE_OFFSET_7] & BYTE_MAX_255));
    }
}
