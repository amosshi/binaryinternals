/*
 * Type_ulong.java    June 17, 2015, 21:40
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

import java.math.BigInteger;

/// 64-bit unsigned int, little-endian.
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
public final class Type_ulong {

    /// Length of the type in bytes.
    public static final int LENGTH = 8;

    /// Value of the DEX `ulong`.
    public final BigInteger value;

    Type_ulong(BigInteger bi) {
        this.value = bi;
    }

    @Override
    public String toString() {
        return "0x%s | %s | %,d".formatted(
                this.value.toString(16).toUpperCase(),
                this.value.toString(),
                this.value.longValue());
    }
}