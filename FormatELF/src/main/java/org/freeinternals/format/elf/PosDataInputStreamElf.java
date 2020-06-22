/*
 * PosDataInputStreamElf.java    Apr 10, 2020
 *
 * Copyright 2020, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.elf;

import java.io.IOException;
import java.math.BigInteger;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.elf.Elf64Type.Elf64_Addr;
import org.freeinternals.format.elf.Elf64Type.Elf64_Half;
import org.freeinternals.format.elf.Elf64Type.Elf64_Off;
import org.freeinternals.format.elf.Elf64Type.Elf64_SHalf;
import org.freeinternals.format.elf.Elf64Type.Elf64_Sword;
import org.freeinternals.format.elf.Elf64Type.Elf64_Sxword;
import org.freeinternals.format.elf.Elf64Type.Elf64_Word;
import org.freeinternals.format.elf.Elf64Type.Elf64_Xword;
import org.freeinternals.format.elf.Identification.Endianness;

/**
 * Extended reader for <code>ELF</code> format.
 *
 * @author Amos Shi
 */
public class PosDataInputStreamElf extends PosDataInputStream {

    protected final Endianness endian;

    PosDataInputStreamElf(PosByteArrayInputStream in, Endianness e) throws IOException {
        super(in);

        // Endianness
        if (e != Endianness.ELFDATA2LSB && e != Endianness.ELFDATA2MSB) {
            // Make sure there is only two Endianness
            throw new IllegalArgumentException("Invalid Endianness in ELF file. value=" + e);
        }
        this.endian = e;

        // Skip the ELF file Identification
        BytesTool.skip(this, Identification.EI_NIDENT);
    }

    /**
     * Read data for {@link Elf64_Addr} (<code>typedef __u64    Elf64_Addr</code>).
     *
     * @return Value in {@link Elf64_Addr}
     * @throws IOException Read data failed
     */
    public Elf64_Addr read_Elf64_Addr() throws IOException {
        BigInteger value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readUnsignedLongInLittleEndian()
                : this.readUnsignedLong();
        return new Elf64_Addr(value);
    }

    /**
     * Read data for {@link Elf64_Half}
     * (<code>typedef __u16	Elf64_Half;</code>).
     *
     * @return Value in {@link Elf64_Half}
     * @throws IOException Read data failed
     */
    public Elf64_Half read_Elf64_Half() throws IOException {
        int value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readUnsignedShortInLittleEndian()
                : this.readUnsignedShort();
        return new Elf64_Half(value);
    }

    /**
     * Read data for {@link Elf64_Off} (<code>typedef __u64	Elf64_Off;</code>).
     *
     * @return Value in {@link Elf64_Off}
     * @throws IOException Read data failed
     */
    public Elf64_Off read_Elf64_Off() throws IOException {
        BigInteger value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readUnsignedLongInLittleEndian()
                : this.readUnsignedLong();
        return new Elf64_Off(value);
    }

    /**
     * Read data for {@link Elf64_SHalf}
     * (<code>typedef __s16	Elf64_SHalf;</code>).
     *
     * @return Value in {@link Elf64_SHalf}
     * @throws IOException Read data failed
     */
    public Elf64_SHalf read_Elf64_SHalf() throws IOException {
        short value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readShortInLittleEndian()
                : this.readShort();
        return new Elf64_SHalf(value);
    }

    /**
     * Read data for {@link Elf64_Sword}
     * (<code>typedef __s32	Elf64_Sword;</code>).
     *
     * @return Value in {@link Elf64_Sword}
     * @throws IOException Read data failed
     */
    public Elf64_Sword read_Elf64_Sword() throws IOException {
        int value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readIntInLittleEndian()
                : this.readInt();
        return new Elf64_Sword(value);
    }

    /**
     * Read data for {@link Elf64_Sxword}
     * (<code>typedef __s64	Elf64_Sxword;</code>).
     *
     * @return Value in {@link Elf64_Sxword}
     * @throws IOException Read data failed
     */
    public Elf64_Sxword read_Elf64_Sxword() throws IOException {
        long value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readLongInLittleEndian()
                : this.readLong();
        return new Elf64_Sxword(value);
    }

    /**
     * Read data for {@link Elf64_Word}
     * (<code>typedef __u32	Elf64_Word;</code>).
     *
     * @return Value in {@link Elf64_Word}
     * @throws IOException Read data failed
     */
    public Elf64_Word read_Elf64_Word() throws IOException {
        long value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readUnsignedIntInLittleEndian()
                : this.readUnsignedInt();
        return new Elf64_Word(value);
    }

    /**
     * Read data for {@link Elf64_Xword}
     * (<code>typedef __u64	Elf64_Xword;</code>).
     *
     * @return Value in {@link Elf64_Xword}
     * @throws IOException Read data failed
     */
    public Elf64_Xword read_Elf64_Xword() throws IOException {
        BigInteger value = (Endianness.ELFDATA2LSB.value == this.endian.value)
                ? this.readUnsignedLongInLittleEndian()
                : this.readUnsignedLong();
        return new Elf64_Xword(value);
    }

}
