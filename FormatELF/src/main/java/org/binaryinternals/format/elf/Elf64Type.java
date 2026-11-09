/*
 * Elf64Type.java    June 21, 2020
 *
 * Copyright 2020, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.elf;

import java.math.BigInteger;

/// 64-bit ELF base types.
///
/// ```
///   typedef __u64    Elf64_Addr;
///   typedef __u16    Elf64_Half;
///   typedef __s16    Elf64_SHalf;
///   typedef __u64    Elf64_Off;
///   typedef __s32    Elf64_Sword;
///   typedef __u32    Elf64_Word;
///   typedef __u64    Elf64_Xword;
///   typedef __s64    Elf64_Sxword;
/// ```
///
/// @author Amos Shi
/// @see
/// <a href="https://github.com/torvalds/linux/blob/master/include/uapi/linux/elf.h">include/uapi/linux/elf.h</a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from ELF C programming language source code
/// ```
@SuppressWarnings("java:S101")
public class Elf64Type {

    private Elf64Type() {
    }

    /// `typedef __u64    Elf64_Addr;`.
    /// @param value The unsigned 64-bit address
    public record Elf64_Addr(BigInteger value) {

        /// Binary data length in bytes.
        public static final int LENGTH = 8;
    }

    /// `typedef __u16    Elf64_Half;`.
    /// @param value The unsigned 16-bit value
    public record Elf64_Half(int value) {
        /// Binary data length in bytes.
        public static final int LENGTH = 2;
    }

    /// `typedef __u64    Elf64_Off;`.
    /// @param value The unsigned 64-bit file offset
    public record Elf64_Off(BigInteger value) {
        /// Binary data length in bytes.
        public static final int LENGTH = 8;
    }

    /// `typedef __s16    Elf64_SHalf;`.
    /// @param value The signed 16-bit value
    public record Elf64_SHalf(short value) {
        /// Binary data length in bytes.
        public static final int LENGTH = 2;
    }

    /// `typedef __s32    Elf64_Sword;`.
    /// @param value The signed 32-bit value
    public record Elf64_Sword(int value) {
        /// Binary data length in bytes.
        public static final int LENGTH = 4;
    }

    /// `typedef __s64    Elf64_Sxword;`.
    /// @param value The signed 64-bit value
    public record Elf64_Sxword(long value) {
        /// Binary data length in bytes.
        public static final int LENGTH = 8;
    }

    /// `typedef __u32    Elf64_Word;`.
    /// @param value The unsigned 32-bit value
    public record Elf64_Word(long value) {
        /// Binary data length in bytes.
        public static final int LENGTH = 4;
    }

    /// `typedef __u64    Elf64_Xword;`.
    /// @param value The unsigned 64-bit value
    public record Elf64_Xword(BigInteger value) {
        /// Binary data length in bytes.
        public static final int LENGTH = 8;
    }

}
