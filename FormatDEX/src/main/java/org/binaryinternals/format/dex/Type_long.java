/*
 * Type_long.java    June 17, 2015, 21:39
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

/// 64-bit signed int, little-endian.
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
public final class Type_long {

    /// Length of the type in bytes.
    public static final int LENGTH = 8;
    /// Value of the DEX `long`.
    public final long value;

    Type_long(long l) {
        this.value = l;
    }

    @Override
    public String toString() {
        return "0x%X | %,d".formatted(this.value, this.value);
    }
}
