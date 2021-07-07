/*
 * Identification.java    Apr 10, 2020
 *
 * Copyright 2020, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.elf;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * ELF header.
 *
 * @author Amos Shi
 *
 * @see
 * <a href="https://github.com/torvalds/linux/blob/master/include/linux/elf.h">include/linux/elf.h</a>
 * @see
 * <a href="https://github.com/torvalds/linux/blob/master/include/linux/elfcore.h">include/linux/elfcore.h</a>
 * @see
 * <a href="https://github.com/torvalds/linux/blob/master/arch/powerpc/boot/elf.h">arch/powerpc/boot/elf.h</a>
 *
 * <pre>
 * java:S116 - Field names should comply with a naming convention --- We respect the name from ELF C programming language source code
 * </pre>
 */
@SuppressWarnings("java:S116")
public class Identification extends FileComponent implements GenerateTreeNode {

    /**
     * Number of bytes in identification.
     *
     * @see
     * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
     */
    public static final int EI_NIDENT = 16;

    /**
     * Magic number of {@code class} file.
     */
    public static final int MAGIC_NUMBER = 0x7F454C46;
    public static final int EI_MAG_SIZE = 4;
    public static final int EI_CLASS_SIZE = 1;
    public static final int EI_DATA_SIZE = 1;
    public static final int EI_VERSION_SIZE = 1;
    public static final int EI_OSABI_SIZE = 1;
    public static final int EI_ABIVERSION_SIZE = 1;
    public static final int EI_PAD_SIZE = 7;

    /**
     * Magic number of ELF file: <code>0x7F454C46</code>.
     *
     * @see {@link #MAGIC_NUMBER}
     */
    public final int EI_MAG;

    /**
     * ELF classification: 32- or 64-bit.
     *
     * @see {@link ElfClass}
     */
    public final ElfClass EI_CLASS;

    /**
     * ELV version.
     */
    public final Version EI_VERSION;

    /**
     * Little or big endianness.
     *
     * @see {@link Endianness}
     */
    public final Endianness EI_DATA;

    /**
     * Target operating system ABI.
     */
    public final OSAbi EI_OSABI;

    /**
     * Further specifies the ABI version. Its interpretation depends on the
     * target ABI {@link #EI_OSABI}. Linux kernel (after at least 2.6) has no
     * definition of it.
     *
     * @see
     * <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
     * and Linkable Format</a>
     * @see
     * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
     */
    public final int EI_ABIVERSION;

    /**
     * Currently unused.
     */
    public final byte[] EI_PAD = new byte[EI_PAD_SIZE];

    Identification(final PosDataInputStream input) throws IOException, FileFormatException {
        this.startPos = input.getPos();

        // Magic number
        this.EI_MAG = input.readInt();
        if (this.EI_MAG != MAGIC_NUMBER) {
            throw new FileFormatException("The magic number is not for ELF file. expected=" + Integer.toHexString(MAGIC_NUMBER) + ", value=" + Integer.toHexString(this.EI_MAG));
        }

        // ELF Class: 32-/64-bit
        int value = input.readUnsignedByte();
        if (!ElfClass.contains(value)) {
            throw new FileFormatException("Unrecognized EI_CLASS value in  ELF file header. value=" + value);
        }
        this.EI_CLASS = ElfClass.valueOf(value);

        // Endianness
        value = input.readUnsignedByte();
        if (!Endianness.contains(value)) {
            throw new FileFormatException("Unrecognized EI_DATA value in  ELF file header. value=" + value);
        }
        this.EI_DATA = Endianness.valueOf(value);

        // Version
        value = input.readUnsignedByte();
        if (!Version.contains(value)) {
            throw new FileFormatException("Unrecognized EI_VERSION value in  ELF file header. value=" + value);
        }
        this.EI_VERSION = Version.valueOf(value);

        // EI_OSABI
        value = input.readUnsignedByte();
        if (!OSAbi.contains(value)) {
            throw new FileFormatException("Unrecognized EI_OSABI value in  ELF file header. value=" + value);
        }
        this.EI_OSABI = OSAbi.valueOf(value);

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

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int currentPos = this.startPos;

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                String.format("ELF File Header [0x%08X, %d]", this.startPos, this.length),
                "The ELF header defines whether to use 32-bit or 64-bit addresses. The header contains three fields that are affected by this setting and offset other fields that follow them."
        ));
        parentNode.add(node);

        // EI_MAG
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_MAG_SIZE,
                String.format("EI_MAG = 0x%08X", this.EI_MAG),
                "0x7F followed by ELF(45 4c 46) in ASCII; these four bytes constitute the magic number.")));
        currentPos += EI_MAG_SIZE;

        // EI_CLASS
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_CLASS_SIZE,
                String.format("EI_CLASS = %d : %s", this.EI_CLASS.value, this.EI_CLASS.name()),
                "Length of target CPU, like 32-bit or 64-bit"
        )));
        currentPos += EI_CLASS_SIZE;

        // EI_DATA
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_DATA_SIZE,
                String.format("EI_DATA = %d : %s", this.EI_DATA.value, this.EI_DATA.toString()),
                "Endianness of number format, like little or big endianness"
        )));
        currentPos += EI_DATA_SIZE;

        // EI_VERSION
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_VERSION_SIZE,
                String.format("EI_VERSION = %d : %s", this.EI_VERSION.value, this.EI_VERSION.name()),
                "Version of ELF file"
        )));
        currentPos += EI_VERSION_SIZE;

        // EI_OSABI
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_OSABI_SIZE,
                String.format("EI_OSABI = %d : %s", this.EI_OSABI.value, this.EI_OSABI.name()),
                "Identifies the target operating system ABI. Zero (0) means regardless of the target platform.")));
        currentPos += EI_OSABI_SIZE;

        // EI_ABIVERSION
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_ABIVERSION_SIZE,
                String.format("EI_ABIVERSION = %d", this.EI_ABIVERSION),
                "Further specifies the ABI version. Its interpretation depends on the target ABI. Linux kernel (after at least 2.6) has no definition of it, so it is ignored for statically-linked executables."
        )));
        currentPos += EI_ABIVERSION_SIZE;

        // EI_PAD
        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                currentPos,
                EI_PAD_SIZE,
                String.format("EI_PAD (length = %d)", EI_PAD_SIZE),
                "Currently unused"
        )));
    }

    public enum ElfClass {
        /**
         * Invalid class.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/arch/powerpc/boot/elf.h">arch/powerpc/boot/elf.h</a>
         */
        ELFCLASSNONE(0),
        /**
         * Current ELF file is 32-bit format.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
         */
        ELFCLASS32(1),
        /**
         * Current ELF file is 64-bit format.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
         */
        ELFCLASS64(2);

        /**
         * Inner value of the enumeration item.
         */
        public final int value;

        private ElfClass(final int i) {
            this.value = i;
        }

        /**
         * Check if a value is a valid {@link ElfClass} enumeration value.
         *
         * @param v Value to test
         * @return <code>true</code> if contains the specified value, else
         * <code>false</code>
         */
        public static boolean contains(int v) {
            boolean result = false;
            for (ElfClass cls : ElfClass.values()) {
                if (cls.value == v) {
                    result = true;
                    break;
                }
            }

            return result;
        }

        /**
         * Returns the enum constant of the specified enum type with the
         * specified name.
         *
         * @param v the value of the constant to return
         * @return the enum constant of the specified enum type with the
         * specified value
         */
        public static ElfClass valueOf(int v) {
            for (ElfClass cls : ElfClass.values()) {
                if (cls.value == v) {
                    return cls;
                }
            }

            throw new IllegalArgumentException(String.format("Unrecognized EI_CLASS value. value=%d", v));
        }
    }

    /**
     * Little or big endianness.
     *
     * @see
     * <a href="https://github.com/torvalds/linux/blob/master/kernel/kexec_elf.c">kernel/kexec_elf.c</a>
     * @see
     * <a href="https://github.com/torvalds/linux/blob/master/arch/x86/um/asm/elf.h">arch/x86/um/asm/elf.h</a>
     *
     */
    public enum Endianness {

        /**
         * Little endianness.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
         */
        ELFDATA2LSB(1, "Little endianness"),
        /**
         * Big endianness.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
         */
        ELFDATA2MSB(2, "Big endianness.");

        /**
         * Inner value of the enumeration item.
         */
        public final int value;

        /**
         * Description of the endianness since the enum name is not readable.
         */
        public final String description;

        private Endianness(int e, String d) {
            this.value = e;
            this.description = d;
        }

        @Override
        public String toString() {
            return this.name() + ", " + this.description;
        }

        /**
         * Check if a value is a valid {@link Endianness} enumeration value.
         *
         * @param v Value to test
         * @return <code>true</code> if contains the specified value, else
         * <code>false</code>
         */
        public static boolean contains(int v) {
            boolean result = false;
            for (Endianness cls : Endianness.values()) {
                if (cls.value == v) {
                    result = true;
                    break;
                }
            }

            return result;
        }

        /**
         * Returns the enum constant of the specified enum type with the
         * specified name.
         *
         * @param v the value of the constant to return
         * @return the enum constant of the specified enum type with the
         * specified value
         */
        public static Endianness valueOf(int v) {
            for (Endianness endian : Endianness.values()) {
                if (endian.value == v) {
                    return endian;
                }
            }

            throw new IllegalArgumentException(String.format("Unrecognized EI_DATA value. value=%d", v));
        }

    }

    /**
     * Set to 1 for the original and current version of ELF.
     */
    public enum Version {
        /**
         * ELV version: current version.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
         */
        EV_CURRENT(1);

        /**
         * Inner value of the enumeration item.
         */
        public final int value;

        private Version(int v) {
            this.value = v;
        }

        /**
         * Check if a value is a valid {@link Version} enumeration value.
         *
         * @param v Value to test
         * @return <code>true</code> if contains the specified value, else
         * <code>false</code>
         */
        public static boolean contains(int v) {
            boolean result = false;
            for (Version ver : Version.values()) {
                if (ver.value == v) {
                    result = true;
                    break;
                }
            }

            return result;
        }

        /**
         * Returns the enum constant of the specified enum type with the
         * specified name.
         *
         * @param v the value of the constant to return
         * @return the enum constant of the specified enum type with the
         * specified value
         */
        public static Version valueOf(int v) {
            for (Version ver : Version.values()) {
                if (ver.value == v) {
                    return ver;
                }
            }

            throw new IllegalArgumentException(String.format("Unrecognized EI_VERSION value. value=%d", v));
        }
    }

    /**
     * Identifies the target operating system ABI.
     *
     * @see
     * <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
     * and Linkable Format</a>
     * @see
     * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
     *
     * <pre>
     * java:S115 - Constant names should comply with a naming convention --> We respect the name from ELF Spec instead
     * </pre>
     */
    @SuppressWarnings("java:S115")
    public enum OSAbi {
        /**
         * Regardless of the target platform. The value <code>0</code> could be
         * either <code>System V</code> or <code>ELFOSABI_NONE</code>. Here we
         * respect the constant name defined in Linux source code located at
         * <code>include/uapi/linux/elf.h</code>.
         *
         * @see
         * <a href="https://en.wikipedia.org/wiki/Executable_and_Linkable_Format">Executable
         * and Linkable Format</a>
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
         */
        ELFOSABI_NONE(0x00),
        HP_UNIX(0x01),
        NetBSD(0x02),
        /**
         * Linux system.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
         */
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
        /**
         * ARM FDPIC platform.
         *
         * @see
         * <a href="https://github.com/torvalds/linux/blob/master/arch/arm/include/asm/elf.h">arch/arm/include/asm/elf.h</a>
         */
        ELFOSABI_ARM_FDPIC(65);

        /**
         * Inner value of the enumeration item.
         */
        public final int value;

        private OSAbi(int v) {
            this.value = v;
        }

        /**
         * Check if a value is a valid {@link Version} enumeration value.
         *
         * @param v Value to test
         * @return <code>true</code> if contains the specified value, else
         * <code>false</code>
         */
        public static boolean contains(int v) {
            boolean result = false;
            for (OSAbi abi : OSAbi.values()) {
                if (abi.value == v) {
                    result = true;
                    break;
                }
            }

            return result;
        }

        /**
         * Returns the enum constant of the specified enum type with the
         * specified name.
         *
         * @param v the value of the constant to return
         * @return the enum constant of the specified enum type with the
         * specified value
         */
        public static OSAbi valueOf(int v) {
            for (OSAbi abi : OSAbi.values()) {
                if (abi.value == v) {
                    return abi;
                }
            }

            throw new IllegalArgumentException(String.format("Unrecognized EI_OSABI value. value=%d", v));
        }
    }

}
