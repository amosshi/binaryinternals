/*
 * ElfFile.java    June 23, 2015, 21:47
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.elf;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormat;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// File for Executable and Linkable Format (ELF).
///
/// ELF file would be different on different CPU architecture. Here we assume
/// the CPU is 64-bit x86 based.
///
/// @author Amos Shi
/// @see <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable and Linkable Format</a>
/// @see <a href="https://github.com/torvalds/linux/blob/master/include/linux/elfcore.h">include/linux/elfcore.h</a>
/// @see <a href="https://github.com/torvalds/linux/blob/master/arch/x86/include/asm/elf.h">arch/x86/include/asm/elf.h</a>
/// @see <a href="https://github.com/torvalds/linux/blob/master/arch/arm64/include/asm/elf.h">arch/arm64/include/asm/elf.h</a>
/// @see <a href="https://github.com/torvalds/linux/blob/master/arch/powerpc/boot/elf.h">arch/powerpc/boot/elf.h</a>
public class ElfFile extends FileFormat {

    /// The ELF identification, the first 16 bytes of the file.
    public final Identification ident;
    /// The ELF header.
    public final Elf64_Ehdr header;

    /// Reads and parses an ELF file.
    ///
    /// @param file The ELF file
    /// @throws IOException if the file cannot be read or parsed
    public ElfFile(final Path file) throws IOException {
        super(file);

        PosDataInputStream input = new PosDataInputStream(new PosByteArrayInputStream(this.fileByteArray));
        this.ident = new Identification(input);

        PosDataInputStreamElf inputElf = new PosDataInputStreamElf(new PosByteArrayInputStream(this.fileByteArray), this.ident.EI_DATA);
        input.skipNBytes(Identification.EI_NIDENT);
        this.header = new Elf64_Ehdr(inputElf);
    }

    @Override
    public String getContentTabName() {
        return "Executable and Linkable Format";
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.ident.generateTreeNode(parentNode);
        this.header.generateTreeNode(parentNode);
    }
}
