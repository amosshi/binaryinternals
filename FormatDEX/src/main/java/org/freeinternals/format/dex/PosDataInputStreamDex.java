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
 */
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
     * Read a {@link Dex_byte} from the input stream.
     *
     * @return a {@link Dex_byte}
     * @throws java.io.IOException I/O error
     */
    public Dex_byte Dex_byte() throws IOException {
        return new Dex_byte(this.readByte());
    }

    /**
     * Read a {@link Dex_ubyte} from the input stream.
     *
     * @return a {@link Dex_ubyte}
     * @throws java.io.IOException I/O error
     */
    public Dex_ubyte Dex_ubyte() throws IOException {
        return new Dex_ubyte(this.readUnsignedByte());
    }

    /**
     * Read a {@link Dex_short} from the input stream.
     *
     * @return a {@link Dex_short}
     * @throws java.io.IOException I/O Error
     */
    public Dex_short Dex_short() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Dex_short(this.readShort());
        } else {
            return new Dex_short(this.readShortInLittleEndian());
        }
    }

    /**
     * Read a {@link Dex_ushort} from the input stream.
     *
     * @return a {@link Dex_ushort}
     * @throws java.io.IOException I/O Error
     */
    public Dex_ushort Dex_ushort() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Dex_ushort(this.readUnsignedShort());
        } else {
            return new Dex_ushort(this.readUnsignedShortInLittleEndian());
        }
    }

    /**
     * Read a {@link Dex_int} from the input stream.
     *
     * @return a {@link Dex_int}
     * @throws java.io.IOException I/O Error
     */
    public Dex_int Dex_int() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Dex_int(this.readInt());
        } else {
            return new Dex_int(this.readIntInLittleEndian());
        }
    }

    /**
     * Read a {@link Dex_uint} from the input stream.
     *
     * @return a {@link Dex_uint}
     * @throws java.io.IOException I/O Error
     */
    public Dex_uint Dex_uint() throws IOException {
        if (this.endian.value == HeaderItem.Endian.ENDIAN_CONSTANT.value) {
            return new Dex_uint(this.readUnsignedInt());
        } else {
            return new Dex_uint(this.readUnsignedIntInLittleEndian());
        }
    }

    /**
     * Read a {@link Dex_long} from the input stream.
     *
     * @return a {@link Dex_long}
     * @throws java.io.IOException I/O Error
     */
    public Dex_long Dex_long() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Dex_long(this.readLong());
        } else {
            return new Dex_long(this.readLongInLittleEndian());
        }
    }

    /**
     * Read a {@link Dex_ulong} from the input stream.
     *
     * @return a {@link Dex_ulong}
     * @throws java.io.IOException I/O Error
     */
    public Dex_ulong Dex_ulong() throws IOException {
        if (this.endian == HeaderItem.Endian.ENDIAN_CONSTANT) {
            return new Dex_ulong(this.readUnsignedLong());
        } else {
            return new Dex_ulong(this.readUnsignedLongInLittleEndian());
        }
    }

    /**
     * Read a {@link Dex_sleb128} from the input stream.
     *
     * @throws java.io.IOException I/O Error
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid LEB128
     * format
     * @return a {@link Dex_sleb128}
     * @see <a href="http://dwarfstd.org/Dwarf3Std.php"> DWARF 3.0 Standard</a>
     */
    public Dex_sleb128 Dex_sleb128() throws IOException, FileFormatException {
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

        return new Dex_sleb128(result, super.getPos() - startPos);
    }

    /**
     * Read a {@link Dex_uleb128} from the input stream.
     *
     * @throws java.io.IOException I/O Error
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid LEB128
     * format
     * @return a {@link Dex_uleb128}
     * @see <a href="http://dwarfstd.org/Dwarf3Std.php"> DWARF 3.0 Standard</a>
     */
    public Dex_uleb128 Dex_uleb128() throws IOException, FileFormatException {
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

        return new Dex_uleb128(result, super.getPos() - startPos);
    }

    /**
     * Read a {@link Dex_uleb128p1} from the input stream.
     *
     * @throws java.io.IOException I/O Error
     * @throws org.freeinternals.commonlib.core.FileFormatException Invalid LEB128
     * format
     * @return a {@link Dex_uleb128p1}
     * @see <a href="http://dwarfstd.org/Dwarf3Std.php"> DWARF 3.0 Standard</a>
     */
    public Dex_uleb128p1 Dex_uleb128p1() throws IOException, FileFormatException {
        Dex_uleb128 uleb128 = this.Dex_uleb128();
        return new Dex_uleb128p1(uleb128.value - 1, uleb128.length);
    }
}
