/*
 * PosDataInputStreamDex.java    June 17, 2015, 21:29
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.format.dex.HeaderItem.Endian;

/**
 *
 * @author Amos Shi
 * 
 * <pre>
 * java:S100 - Method names should comply with a naming convention --- We use different naming convention for better readablity
 * </pre>
 */
@SuppressWarnings("java:S100")
public class PosDataInputStreamDex extends PosDataInputStream {

    /**
     * Endian of the {@link DexFile}. The default value is little-endian
     * {@link HeaderItem.Endian#ENDIAN_CONSTANT}, as the DEX format
     * specification said.
     */
    protected final HeaderItem.Endian endian;

    public PosDataInputStreamDex(PosByteArrayInputStream in) {
        super(in);
        this.endian = Endian.ENDIAN_CONSTANT;
    }

    public PosDataInputStreamDex(PosByteArrayInputStream in, HeaderItem.Endian e) {
        super(in);
        this.endian = e;
    }

    /**
     * Read a {@link Type_byte} from the input stream.
     *
     * @return a {@link Type_byte}
     * @throws java.io.IOException I/O error
     */
    public Type_byte Dex_byte() throws IOException {
        return new Type_byte(this.readByte());
    }

    /**
     * Read a {@link Type_ubyte} from the input stream.
     *
     * @return a {@link Type_ubyte}
     * @throws java.io.IOException I/O error
     */
    public Type_ubyte Dex_ubyte() throws IOException {
        return new Type_ubyte(this.readUnsignedByte());
    }

    /**
     * Read a {@link Type_short} from the input stream.
     *
     * @return a {@link Type_short}
     * @throws java.io.IOException I/O Error
     */
    public Type_short Dex_short() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Type_short(this.readShort());
        } else {
            return new Type_short(this.readShortInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_ushort} from the input stream.
     *
     * @return a {@link Type_ushort}
     * @throws java.io.IOException I/O Error
     */
    public Type_ushort Dex_ushort() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Type_ushort(this.readUnsignedShort());
        } else {
            return new Type_ushort(this.readUnsignedShortInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_int} from the input stream.
     *
     * @return a {@link Type_int}
     * @throws java.io.IOException I/O Error
     */
    public Type_int Dex_int() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Type_int(this.readInt());
        } else {
            return new Type_int(this.readIntInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_uint} from the input stream.
     *
     * @return a {@link Type_uint}
     * @throws java.io.IOException I/O Error
     */
    public Type_uint Dex_uint() throws IOException {
        if (this.endian.value == HeaderItem.Endian.ENDIAN_CONSTANT.value) {
            return new Type_uint(this.readUnsignedInt());
        } else {
            return new Type_uint(this.readUnsignedIntInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_long} from the input stream.
     *
     * @return a {@link Type_long}
     * @throws java.io.IOException I/O Error
     */
    public Type_long Dex_long() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Type_long(this.readLong());
        } else {
            return new Type_long(this.readLongInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_ulong} from the input stream.
     *
     * @return a {@link Type_ulong}
     * @throws java.io.IOException I/O Error
     */
    public Type_ulong Dex_ulong() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Type_ulong(this.readUnsignedLong());
        } else {
            return new Type_ulong(this.readUnsignedLongInLittleEndian());
        }
    }

    /**
     * Read a {@link Type_sleb128} from the input stream.
     *
     * @throws java.io.IOException I/O Error
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
     * @throws java.io.IOException I/O Error
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
     * @throws java.io.IOException I/O Error
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid LEB128
     * format
     * @return a {@link Type_uleb128p1}
     */
    public Type_uleb128p1 Dex_uleb128p1() throws IOException, FileFormatException {
        Type_uleb128 uleb128 = this.Dex_uleb128();
        return new Type_uleb128p1(uleb128.value - 1, uleb128.length);
    }
}
