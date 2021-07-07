/*
 * elf64_hdr.java    Apr 10, 2020
 *
 * Copyright 2020, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.elf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.elf.Elf64Type.Elf64_Addr;
import org.freeinternals.format.elf.Elf64Type.Elf64_Half;
import org.freeinternals.format.elf.Elf64Type.Elf64_Off;
import org.freeinternals.format.elf.Elf64Type.Elf64_Word;

/**
 * ELF header for 64-bit system. In this class definition, we respect the name
 * defined in Linux source code.
 *
 * <pre>
 *   typedef struct elf64_hdr {
 *     unsigned char	e_ident[EI_NIDENT];      // ELF "magic number"
 *     Elf64_Half e_type;
 *     Elf64_Half e_machine;
 *     Elf64_Word e_version;
 *     Elf64_Addr e_entry;		         // Entry point virtual address
 *     Elf64_Off e_phoff;	         	 // Program header table file offset
 *     Elf64_Off e_shoff         ;		 // Section header table file offset
 *     Elf64_Word e_flags;
 *     Elf64_Half e_ehsize;
 *     Elf64_Half e_phentsize;
 *     Elf64_Half e_phnum;
 *     Elf64_Half e_shentsize;
 *     Elf64_Half e_shnum;
 *     Elf64_Half e_shstrndx;
 *   } Elf64_Ehdr;
 * </pre>
 *
 * @author Amos Shi
 *
 * @see
 * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from ELF C programming language source code
 * java:S116 - Field names should comply with a naming convention --- We respect the name from ELF C programming language source code
 * </pre>
 */
@SuppressWarnings({"java:S101", "java:S116"})
public class Elf64_Ehdr extends FileComponent implements GenerateTreeNode {

    /**
     * ELF file types.
     */
    public final Elf64_Half e_type;
    public final Elf64_Half e_machine;
    public final Elf64_Word e_version;
    /**
     * Entry point virtual address
     */
    public final Elf64_Addr e_entry;
    /**
     * Program header table file offset
     */
    public final Elf64_Off e_phoff;
    /**
     * Section header table file offset
     */
    public final Elf64_Off e_shoff;
    public final Elf64_Word e_flags;
    public final Elf64_Half e_ehsize;
    public final Elf64_Half e_phentsize;
    public final Elf64_Half e_phnum;
    public final Elf64_Half e_shentsize;
    public final Elf64_Half e_shnum;
    public final Elf64_Half e_shstrndx;

    Elf64_Ehdr(final PosDataInputStreamElf input) throws IOException, FileFormatException {
        super.startPos = input.getPos();

        this.e_type = input.read_Elf64_Half();
        this.e_machine = input.read_Elf64_Half();
        this.e_version = input.read_Elf64_Word();
        this.e_entry = input.read_Elf64_Addr();
        this.e_phoff = input.read_Elf64_Off();
        this.e_shoff = input.read_Elf64_Off();
        this.e_flags = input.read_Elf64_Word();
        this.e_ehsize = input.read_Elf64_Half();
        this.e_phentsize = input.read_Elf64_Half();
        this.e_phnum = input.read_Elf64_Half();
        this.e_shentsize = input.read_Elf64_Half();
        this.e_shnum = input.read_Elf64_Half();
        this.e_shstrndx = input.read_Elf64_Half();

        super.length = input.getPos() - super.startPos;
    }

    /**
     * Enum type for field {@link #e_type}.
     *
     * <pre>
     *   defined in: include/uapi/linux/elf.h
     *
     *   #define ET_NONE   0
     *   #define ET_REL    1
     *   #define ET_EXEC   2
     *   #define ET_DYN    3
     *   #define ET_CORE   4
     *   #define ET_LOPROC 0xff00
     *   #define ET_HIPROC 0xffff
     * </pre>
     *
     * <pre>
     *   defined in: arch/powerpc/boot/elf.h
     *
     *   #define ET_NONE   0
     *   #define ET_REL    1
     *   #define ET_EXEC   2
     *   #define ET_DYN    3
     *   #define ET_CORE   4
     *   #define ET_LOPROC 0xff00
     *   #define ET_HIPROC 0xffff
     * </pre>
     *
     * @see
     * <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
     * and Linkable Format</a>
     * @see
     * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
     * @see
     * <a href="https://github.com/torvalds/linux/blob/master/arch/powerpc/boot/elf.h">arch/powerpc/boot/elf.h</a>
     */
    public enum ELF_FileType {
        ET_NONE(0x00),
        ET_REL(0x01),
        ET_EXEC(0x02),
        ET_DYN(0x03),
        ET_CORE(0x04),
        ET_LOOS(0xfe00),
        ET_HIOS(0xfeff),
        ET_LOPROC(0xff00),
        ET_HIPROC(0xffff);

        /**
         * Inner value of the enumeration item.
         */
        public final int value;

        private ELF_FileType(final int i) {
            this.value = i;
        }

        /**
         * Name of the value.
         *
         * @param v {@link #value} of an enum
         * @return Enum name if found, else <code>Unknown</code>
         */
        public static String nameOf(int v) {
            for (ELF_FileType item : ELF_FileType.values()) {
                if (item.value == v) {
                    return item.name();
                }
            }

            return ELF_FileType.class.getName() + " Unknown";
        }
    }

    /**
     * Enum type for field {@link #e_machine}.
     *
     * @see
     * <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
     * and Linkable Format</a>
     *
     * <pre>
     * java:S115 - Constant names should comply with a naming convention --> We respect the name from ELF Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S115")
    public enum ELF_Machine {
        /**
         * No specific instruction set.
         */
        NotSet(0x00),

        /**
         * SPARC.
         *
         * @see <a href="https://en.wikipedia.org/wiki/SPARC">SPARC</a>
         */
        SPARC(0x02),

        /**
         * x86.
         *
         * @see <a href="https://en.wikipedia.org/wiki/X86">x86</a>
         */
        x86(0x03),

        /**
         * MIPS.
         *
         * @see <a href="https://en.wikipedia.org/wiki/MIPS_architecture">MIPS</a>
         */
        MIPS(0x08),

        /**
         * PowerPC.
         *
         * @see <a href="https://en.wikipedia.org/wiki/PowerPC">PowerPC</a>
         */
        PowerPC(0x14),

        /**
         * PowerPC (64-bit).
         *
         * @see <a href="https://en.wikipedia.org/wiki/PowerPC">PowerPC (64-bit)</a>
         */
        PowerPC64(0x15),

        /**
         * S390, including S390x.
         *
         * @see <a href="https://en.wikipedia.org/wiki/Z/Architecture">S390</a>
         */
        S390(0x16),

        /**
         * ARM.
         *
         * @see <a href="https://en.wikipedia.org/wiki/ARM_architecture">ARM</a>
         */
        ARM(0x28),

        /**
         * SuperH.
         *
         * @see <a href="https://en.wikipedia.org/wiki/SuperH">SuperH</a>
         */
        SuperH(0x2A),

        /**
         * IA-64.
         *
         * @see <a href="https://en.wikipedia.org/wiki/SPARC">IA-64</a>
         */
        IA64(0x32),

        /**
         * amd64.
         *
         * @see <a href="https://en.wikipedia.org/wiki/Amd64">amd64</a>
         */
        amd64(0x3E),

        /**
         * TMS320C6000 Family.
         *
         * @see <a href="https://en.wikipedia.org/wiki/Texas_Instruments_TMS320#C6000_series">TMS320C6000 Family</a>
         */
        TMS320C6000(0x8C),

        /**
         * AArch64.
         *
         * @see <a href="https://en.wikipedia.org/wiki/AArch64">AArch64</a>
         */
        AArch64(0xB7),

        /**
         * RISC-V.
         *
         * @see <a href="https://en.wikipedia.org/wiki/RISC-V">RISC-V</a>
         */
        RISC_V(0xF3);

        /**
         * Inner value of the enumeration item.
         */
        public final int value;

        private ELF_Machine(final int i) {
            this.value = i;
        }

        /**
         * Name of the value.
         *
         * @param v {@link #value} of an enum
         * @return Enum name if found, else <code>Unknown</code>
         */
        public static String nameOf(int v) {
            for (ELF_Machine item : ELF_Machine.values()) {
                if (item.value == v) {
                    return item.name();
                }
            }

            return ELF_Machine.class.getName() + " Unknown";
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int currentPos = this.startPos;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                String.format("ELF64 Header [0x%08X, %d]", this.startPos, this.length),
                "The ELF header on 64-bit CPU architecture."
        ));
        parentNode.add(node);

        // e_type
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_type = 0x%04X (%d): %s", this.e_type.value, this.e_type.value, ELF_FileType.nameOf(this.e_type.value)),
                "Identifies object file type.")));
        currentPos += Elf64_Half.LENGTH;

        // e_machine
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_machine = 0x%04X (%d): %s", this.e_machine.value, this.e_machine.value, ELF_Machine.nameOf(this.e_machine.value)),
                "Specifies target instruction set architecture.")));
        currentPos += Elf64_Half.LENGTH;

        // e_version
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Word.LENGTH,
                String.format("e_version = 0x%08X (%d)", this.e_version.value, this.e_version.value),
                "Set to 1 for the original version of ELF.")));
        currentPos += Elf64_Word.LENGTH;

        // e_entry
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Addr.LENGTH,
                String.format("e_entry = 0x%016X (%d)", this.e_entry.value, this.e_entry.value),
                "This is the memory address of the entry point from where the process starts executing. This field is either 32 or 64 bits long depending on the format defined earlier.")));
        currentPos += Elf64_Addr.LENGTH;

        // e_phoff
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Off.LENGTH,
                String.format("e_phoff = 0x%016X (%d)", this.e_phoff.value, this.e_phoff.value),
                "Points to the start of the program header table. It usually follows the file header immediately, making the offset 0x34 or 0x40 for 32- and 64-bit ELF executables, respectively.")));
        currentPos += Elf64_Off.LENGTH;

        // e_shoff
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Off.LENGTH,
                String.format("e_shoff = 0x%016X (%d)", this.e_shoff.value, this.e_shoff.value),
                "Points to the start of the section header table.")));
        currentPos += Elf64_Off.LENGTH;

        // e_flags
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Word.LENGTH,
                String.format("e_flags = 0x%08X (%d)", this.e_flags.value, this.e_flags.value),
                "Interpretation of this field depends on the target architecture.")));
        currentPos += Elf64_Word.LENGTH;

        // e_ehsize
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_ehsize = 0x%04X (%d)", this.e_ehsize.value, this.e_ehsize.value),
                "Contains the size of this header, normally 64 Bytes for 64-bit and 52 Bytes for 32-bit format.")));
        currentPos += Elf64_Half.LENGTH;

        // e_phentsize
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_phentsize = 0x%04X (%d)", this.e_phentsize.value, this.e_phentsize.value),
                "Contains the size of a program header table entry.")));
        currentPos += Elf64_Half.LENGTH;

        // e_phnum
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_phnum = 0x%04X (%d)", this.e_phnum.value, this.e_phnum.value),
                "Contains the number of entries in the program header table.")));
        currentPos += Elf64_Half.LENGTH;

        // e_shentsize
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_shentsize = 0x%04X (%d)", this.e_shentsize.value, this.e_shentsize.value),
                "Contains the size of a section header table entry.")));
        currentPos += Elf64_Half.LENGTH;

        // e_shnum
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_shnum = 0x%04X (%d)", this.e_shnum.value, this.e_shnum.value),
                "Contains the number of entries in the section header table.")));
        currentPos += Elf64_Half.LENGTH;

        // e_shstrndx
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                Elf64_Half.LENGTH,
                String.format("e_shstrndx = 0x%04X (%d)", this.e_shstrndx.value, this.e_shstrndx.value),
                "Contains index of the section header table entry that contains the section names.")));
    }
}
