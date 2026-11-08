/*
 * Type_byte.java    June 17, 2015, 21:32
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

/// 8-bit signed int.
///
/// @author Amos Shi
/// @see
/// <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
/// Dalvik Executable (DEX) format</a>
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We need different Name convesins for readability
/// ```
@SuppressWarnings("java:S101")
public final class Type_byte {

    /// Length of the type in bytes.
    public static final int LENGTH = 1;

    /// Value of the DEX `byte`.
    ///
    /// @see
    /// <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2.1">
    /// Java byte type</a>
    public final byte value;

    Type_byte(byte b) {
        this.value = b;
    }

    @Override
    public String toString() {
        return "0x%X | %d | %c".formatted((int) this.value, this.value, this.value);
    }
}
