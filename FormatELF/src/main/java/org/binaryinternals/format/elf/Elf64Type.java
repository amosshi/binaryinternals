/*
 * Elf64Type.java    June 21, 2020
 *
 * Copyright 2020, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.elf;

import java.math.BigInteger;

/**
 * 64-bit ELF base types.
 *
 * <pre>
 *   typedef __u64    Elf64_Addr;
 *   typedef __u16    Elf64_Half;
 *   typedef __s16    Elf64_SHalf;
 *   typedef __u64    Elf64_Off;
 *   typedef __s32    Elf64_Sword;
 *   typedef __u32    Elf64_Word;
 *   typedef __u64    Elf64_Xword;
 *   typedef __s64    Elf64_Sxword;
 * </pre>
 *
 * @author Amos Shi
 * @see
 * <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from ELF C programming language source code
 * </pre>
 */
@SuppressWarnings("java:S101")
public class Elf64Type {

    private Elf64Type() {
    }

    /**
     * <code>typedef __u64    Elf64_Addr;</code>.
     */
    public static class Elf64_Addr {

        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 8;
        public final BigInteger value;

        Elf64_Addr(BigInteger big) {
            this.value = big;
        }
    }

    /**
     * <code>typedef __u16    Elf64_Half;</code>.
     */
    public static class Elf64_Half {
        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 2;
        public final int value;

        Elf64_Half(int i) {
            this.value = i;
        }
    }

    /**
     * <code>typedef __u64    Elf64_Off;</code>.
     */
    public static class Elf64_Off {
        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 8;
        public final BigInteger value;

        Elf64_Off(BigInteger big) {
            this.value = big;
        }
    }

    /**
     * <code>typedef __s16    Elf64_SHalf;</code>.
     */
    public static class Elf64_SHalf {
        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 2;
        public final short value;

        Elf64_SHalf(short s) {
            this.value = s;
        }
    }

    /**
     * <code>typedef __s32    Elf64_Sword;</code>.
     */
    public static class Elf64_Sword {
        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 4;
        public final int value;

        Elf64_Sword(int i) {
            this.value = i;
        }
    }

    /**
     * <code>typedef __s64    Elf64_Sxword;</code>.
     */
    public static class Elf64_Sxword {
        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 8;
        public final long value;

        Elf64_Sxword(long l) {
            this.value = l;
        }
    }

    /**
     * <code>typedef __u32    Elf64_Word;</code>.
     */
    public static class Elf64_Word {
        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 4;
        public final long value;

        Elf64_Word(long l) {
            this.value = l;
        }
    }

    /**
     * <code>typedef __u64    Elf64_Xword;</code>.
     */
    public static class Elf64_Xword {
        /**
         * Binary data length in bytes.
         */
        public static final int LENGTH = 8;
        public final BigInteger value;

        Elf64_Xword(BigInteger big) {
            this.value = big;
        }
    }

}
