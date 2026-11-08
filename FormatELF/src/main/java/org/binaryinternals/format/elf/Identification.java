/*
 * Identification.java    Apr 10, 2020
 *
 * Copyright 2020, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.elf;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileComponent;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.GenerateTreeNode;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// ELF header.
///
/// @author Amos Shi
///
/// @see
/// <a href="https://github.com/torvalds/linux/blob/master/include/linux/elf.h">include/linux/elf.h</a>
/// @see
/// <a href="https://github.com/torvalds/linux/blob/master/include/linux/elfcore.h">include/linux/elfcore.h</a>
/// @see
/// <a href="https://github.com/torvalds/linux/blob/master/arch/powerpc/boot/elf.h">arch/powerpc/boot/elf.h</a>
///
/// ```
/// java:S116 - Field names should comply with a naming convention --- We respect the name from ELF C programming language source code
/// ```
@SuppressWarnings("java:S116")
public class Identification extends FileComponent implements GenerateTreeNode {

    /// Number of bytes in identification.
    ///
    /// @see
    /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
    public static final int EI_NIDENT = 16;

    /// Magic number of `class` file.
    public static final int MAGIC_NUMBER = 0x7F454C46;
    public static final int EI_MAG_SIZE = 4;
    public static final int EI_CLASS_SIZE = 1;
    public static final int EI_DATA_SIZE = 1;
    public static final int EI_VERSION_SIZE = 1;
    public static final int EI_OSABI_SIZE = 1;
    public static final int EI_ABIVERSION_SIZE = 1;
    public static final int EI_PAD_SIZE = 7;

    /// Magic number of ELF file: `0x7F454C46`.
    ///
    /// @see #MAGIC_NUMBER
    public final int EI_MAG;

    /// ELF classification: 32- or 64-bit.
    ///
    /// @see ElfClass
    public final ElfClass EI_CLASS;

    /// ELV version.
    public final Version EI_VERSION;

    /// Little or big endianness.
    ///
    /// @see Endianness
    public final Endianness EI_DATA;

    /// Target operating system ABI.
    public final OSAbi EI_OSABI;

    /// Further specifies the ABI version. Its interpretation depends on the
    /// target ABI [#EI_OSABI]. Linux kernel (after at least 2.6) has no
    /// definition of it.
    ///
    /// @see
    /// <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
    /// and Linkable Format</a>
    /// @see
    /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
    public final int EI_ABIVERSION;

    /// Currently unused.
    public final byte[] EI_PAD = new byte[EI_PAD_SIZE];

    Identification(final PosDataInputStream input) throws IOException {
        this.startPos = input.getPos();

        // Magic number
        this.EI_MAG = input.readInt();
        if (this.EI_MAG != MAGIC_NUMBER) {
            throw new FileFormatException("The magic number is not for ELF file. expected=%x, value=%x".formatted(MAGIC_NUMBER, this.EI_MAG));
        }

        // ELF Class: 32-/64-bit
        this.EI_CLASS = readEnum(input, ElfClass::of, "EI_CLASS");

        // Endianness
        this.EI_DATA = readEnum(input, Endianness::of, "EI_DATA");

        // Version
        this.EI_VERSION = readEnum(input, Version::of, "EI_VERSION");

        // EI_OSABI
        this.EI_OSABI = readEnum(input, OSAbi::of, "EI_OSABI");

        // EI_ABIVERSION
        this.EI_ABIVERSION = input.readUnsignedByte();

        // EI_PAD
        final int padLen = input.read(this.EI_PAD);
        if (padLen != EI_PAD_SIZE) {
            throw new FileFormatException("Failed to read EI_PAD value in  ELF file header. length=" + padLen);
        }

        this.length = input.getPos();

        if (this.length != EI_NIDENT) {
            throw new FileFormatException("Invalid length of ELF file identification. expected=" + EI_NIDENT + ", length=" + this.length);
        }
    }

    private static <T> T readEnum(final PosDataInputStream input, final IntFunction<Optional<T>> lookup,
            final String field)
                    throws IOException {
        final int value = input.readUnsignedByte();
        return lookup.apply(value).orElseThrow(
                () -> new FileFormatException("Unrecognized " + field + " value in  ELF file header. value=" + value));
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int currentPos = this.startPos;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "ELF File Header [0x%08X, %d]".formatted(this.startPos, this.length),
                "The ELF header defines whether to use 32-bit or 64-bit addresses. The header contains three fields that are affected by this setting and "
                        + "offset other fields that follow them."
        ));
        parentNode.add(node);

        // EI_MAG
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_MAG_SIZE,
                "EI_MAG = 0x%08X".formatted(this.EI_MAG),
                "0x7F followed by ELF(45 4c 46) in ASCII; these four bytes constitute the magic number.")));
        currentPos += EI_MAG_SIZE;

        // EI_CLASS
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_CLASS_SIZE,
                "EI_CLASS = %d : %s".formatted(this.EI_CLASS.value, this.EI_CLASS.name()),
                "Length of target CPU, like 32-bit or 64-bit"
        )));
        currentPos += EI_CLASS_SIZE;

        // EI_DATA
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_DATA_SIZE,
                "EI_DATA = %d : %s".formatted(this.EI_DATA.value, this.EI_DATA.toString()),
                "Endianness of number format, like little or big endianness"
        )));
        currentPos += EI_DATA_SIZE;

        // EI_VERSION
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_VERSION_SIZE,
                "EI_VERSION = %d : %s".formatted(this.EI_VERSION.value, this.EI_VERSION.name()),
                "Version of ELF file"
        )));
        currentPos += EI_VERSION_SIZE;

        // EI_OSABI
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_OSABI_SIZE,
                "EI_OSABI = %d : %s".formatted(this.EI_OSABI.value, this.EI_OSABI.name()),
                "Identifies the target operating system ABI. Zero (0) means regardless of the target platform.")));
        currentPos += EI_OSABI_SIZE;

        // EI_ABIVERSION
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_ABIVERSION_SIZE,
                "EI_ABIVERSION = %d".formatted(this.EI_ABIVERSION),
                "Further specifies the ABI version. Its interpretation depends on the target ABI. Linux kernel (after at least 2.6) has no definition of it, "
                        + "so it is ignored for statically-linked executables."
        )));
        currentPos += EI_ABIVERSION_SIZE;

        // EI_PAD
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_PAD_SIZE,
                "EI_PAD (length = %d)".formatted(EI_PAD_SIZE),
                "Currently unused"
        )));
    }

    public enum ElfClass {
        /// Invalid class.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/arch/powerpc/boot/elf.h">arch/powerpc/boot/elf.h</a>
        ELFCLASSNONE(0),
        /// Current ELF file is 32-bit format.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
        ELFCLASS32(1),
        /// Current ELF file is 64-bit format.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
        ELFCLASS64(2);

        /// Inner value of the enumeration item.
        public final int value;

        ElfClass(final int i) {
            this.value = i;
        }

        /// Find the [ElfClass] by its [#value].
        ///
        /// @param v Value to look up
        /// @return The matching constant, or empty if not recognized
        public static Optional<ElfClass> of(final int v) {
            return Arrays.stream(values()).filter(e -> e.value == v).findFirst();
        }
    }

    /// Little or big endianness.
    ///
    /// @see
    /// <a href="https://github.com/torvalds/linux/blob/master/kernel/kexec_elf.c">kernel/kexec_elf.c</a>
    /// @see
    /// <a href="https://github.com/torvalds/linux/blob/master/arch/x86/um/asm/elf.h">arch/x86/um/asm/elf.h</a>
    public enum Endianness {

        /// Little endianness.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
        ELFDATA2LSB(1, "Little endianness"),
        /// Big endianness.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
        ELFDATA2MSB(2, "Big endianness.");

        /// Inner value of the enumeration item.
        public final int value;

        /// Description of the endianness since the enum name is not readable.
        public final String description;

        Endianness(int e, String d) {
            this.value = e;
            this.description = d;
        }

        @Override
        public String toString() {
            return this.name() + ", " + this.description;
        }

        /// Find the [Endianness] by its [#value].
        ///
        /// @param v Value to look up
        /// @return The matching constant, or empty if not recognized
        public static Optional<Endianness> of(final int v) {
            return Arrays.stream(values()).filter(e -> e.value == v).findFirst();
        }

    }

    /// Set to 1 for the original and current version of ELF.
    public enum Version {
        /// ELV version: current version.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
        EV_CURRENT(1);

        /// Inner value of the enumeration item.
        public final int value;

        Version(int v) {
            this.value = v;
        }

        /// Find the [Version] by its [#value].
        ///
        /// @param v Value to look up
        /// @return The matching constant, or empty if not recognized
        public static Optional<Version> of(final int v) {
            return Arrays.stream(values()).filter(e -> e.value == v).findFirst();
        }
    }

    /// Identifies the target operating system ABI.
    ///
    /// @see
    /// <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
    /// and Linkable Format</a>
    /// @see
    /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
    ///
    /// ```
    /// java:S115 - Constant names should comply with a naming convention --- We respect the name from ELF Spec instead
    /// ```
    @SuppressWarnings("java:S115")
    public enum OSAbi {
        /// Regardless of the target platform. The value `0` could be
        /// either `System V` or `ELFOSABI_NONE`. Here we
        /// respect the constant name defined in Linux source code located at
        /// `include/uapi/linux/elf.h`.
        ///
        /// @see
        /// <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
        /// and Linkable Format</a>
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
        ELFOSABI_NONE(0x00),
        HP_UNIX(0x01),
        NetBSD(0x02),
        /// Linux system.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
        ELFOSABI_LINUX(0x03),
        GNU_Hurd(0x04),
        Solaris(0x06),
        AIX(0x07),
        IRIX(0x08),
        FreeBSD(0x09),
        Tru64(0x0A),
        Novell_Modesto(0x0B),
        OpenBSD(0x0C),
        OpenVMS(0x0D),
        NonStop_Kernel(0x0E),
        AROS(0x0F),
        Fenix_OS(0x10),
        CloudABI(0x11),
        Stratus_Technologies_OpenVOS(0x12),
        /// ARM FDPIC platform.
        ///
        /// @see
        /// <a href="https://github.com/torvalds/linux/blob/master/arch/arm/include/asm/elf.h">arch/arm/include/asm/elf.h</a>
        ELFOSABI_ARM_FDPIC(65);

        /// Inner value of the enumeration item.
        public final int value;

        OSAbi(int v) {
            this.value = v;
        }

        /// Find the [OSAbi] by its [#value].
        ///
        /// @param v Value to look up
        /// @return The matching constant, or empty if not recognized
        public static Optional<OSAbi> of(final int v) {
            return Arrays.stream(values()).filter(e -> e.value == v).findFirst();
        }
    }

}
