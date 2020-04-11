/*
 * PosDataInputStreamElf.java    Apr 10, 2020
 *
 * Copyright 2020, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.elf;

import java.io.IOException;
import org.freeinternals.commonlib.core.BytesTool;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.elf.Identification.Endianness;

/**
 * Extended reader for <code>ELF</code> format.
 *
 * @author Amos Shi
 */
public class PosDataInputStreamElf extends PosDataInputStream {

    protected final Endianness endian;

    public PosDataInputStreamElf(PosByteArrayInputStream in, Endianness  e) throws IOException {
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

    public Elf64_Addr read_Elf64_Addr() throws IOException {
        if (Endianness.ELFDATA2LSB.value == this.endian.value) {
            return new Elf64_Addr(this.readUnsignedLongInLittleEndian());
        } else {
            return new Elf64_Addr(this.readUnsignedLong());
        }
    }
}
