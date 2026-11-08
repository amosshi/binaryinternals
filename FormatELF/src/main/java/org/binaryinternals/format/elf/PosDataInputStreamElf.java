/*
 * PosDataInputStreamElf.java    Apr 10, 2020
 *
 * Copyright 2020, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.elf;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;
import org.binaryinternals.format.elf.Elf64Type.Elf64_Addr;
import org.binaryinternals.format.elf.Elf64Type.Elf64_Half;
import org.binaryinternals.format.elf.Elf64Type.Elf64_Off;
import org.binaryinternals.format.elf.Elf64Type.Elf64_SHalf;
import org.binaryinternals.format.elf.Elf64Type.Elf64_Sword;
import org.binaryinternals.format.elf.Elf64Type.Elf64_Sxword;
import org.binaryinternals.format.elf.Elf64Type.Elf64_Word;
import org.binaryinternals.format.elf.Elf64Type.Elf64_Xword;
import org.binaryinternals.format.elf.Identification.Endianness;

/// Extended reader for `ELF` format.
///
/// @author Amos Shi
///
/// ```
/// java:S100 - Method names should comply with a naming convention --- We respect the name from ELF spec instead
/// ```
@SuppressWarnings("java:S100")
public class PosDataInputStreamElf extends PosDataInputStream {

    protected final Endianness endian;

    private final ByteOrder byteOrder;

    PosDataInputStreamElf(PosByteArrayInputStream in, Endianness e) throws IOException {
        super(in);

        this.endian = e;
        this.byteOrder = switch (e) {
            case ELFDATA2LSB -> ByteOrder.LITTLE_ENDIAN;
            case ELFDATA2MSB -> ByteOrder.BIG_ENDIAN;
        };

        // Skip the ELF file Identification
        this.skipNBytes(Identification.EI_NIDENT);
    }

    /// Read data for [Elf64_Addr] (`typedef __u64    Elf64_Addr`).
    ///
    /// @return Value in [Elf64_Addr]
    /// @throws IOException Read data failed
    public Elf64_Addr read_Elf64_Addr() throws IOException {
        return new Elf64_Addr(this.readUnsignedLong(this.byteOrder));
    }

    /// Read data for [Elf64_Half]
    /// (`typedef __u16	Elf64_Half;`).
    ///
    /// @return Value in [Elf64_Half]
    /// @throws IOException Read data failed
    public Elf64_Half read_Elf64_Half() throws IOException {
        return new Elf64_Half(this.readUnsignedShort(this.byteOrder));
    }

    /// Read data for [Elf64_Off] (`typedef __u64	Elf64_Off;`).
    ///
    /// @return Value in [Elf64_Off]
    /// @throws IOException Read data failed
    public Elf64_Off read_Elf64_Off() throws IOException {
        return new Elf64_Off(this.readUnsignedLong(this.byteOrder));
    }

    /// Read data for [Elf64_SHalf]
    /// (`typedef __s16	Elf64_SHalf;`).
    ///
    /// @return Value in [Elf64_SHalf]
    /// @throws IOException Read data failed
    public Elf64_SHalf read_Elf64_SHalf() throws IOException {
        return new Elf64_SHalf(this.readShort(this.byteOrder));
    }

    /// Read data for [Elf64_Sword]
    /// (`typedef __s32	Elf64_Sword;`).
    ///
    /// @return Value in [Elf64_Sword]
    /// @throws IOException Read data failed
    public Elf64_Sword read_Elf64_Sword() throws IOException {
        return new Elf64_Sword(this.readInt(this.byteOrder));
    }

    /// Read data for [Elf64_Sxword]
    /// (`typedef __s64	Elf64_Sxword;`).
    ///
    /// @return Value in [Elf64_Sxword]
    /// @throws IOException Read data failed
    public Elf64_Sxword read_Elf64_Sxword() throws IOException {
        return new Elf64_Sxword(this.readLong(this.byteOrder));
    }

    /// Read data for [Elf64_Word]
    /// (`typedef __u32	Elf64_Word;`).
    ///
    /// @return Value in [Elf64_Word]
    /// @throws IOException Read data failed
    public Elf64_Word read_Elf64_Word() throws IOException {
        return new Elf64_Word(this.readUnsignedInt(this.byteOrder));
    }

    /// Read data for [Elf64_Xword]
    /// (`typedef __u64	Elf64_Xword;`).
    ///
    /// @return Value in [Elf64_Xword]
    /// @throws IOException Read data failed
    public Elf64_Xword read_Elf64_Xword() throws IOException {
        return new Elf64_Xword(this.readUnsignedLong(this.byteOrder));
    }

}
