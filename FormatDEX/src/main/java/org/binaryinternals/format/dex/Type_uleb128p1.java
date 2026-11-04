/*
 * Type_uleb128p1.java    June 17, 2015, 21:42
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

/// Unsigned LEB128 plus 1, variable-length.
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
public final class Type_uleb128p1 {

    /// Value of the DEX `uleb128`.
    public final int value;
    /// Length of the number in DEX file in bytes.
    public final int length;

    Type_uleb128p1(int v, int l) {
        this.value = v;
        this.length = l;
    }

    @Override
    public String toString() {
        return "length=%d value=0x%s | %,d".formatted(this.length, Integer.toHexString(this.value).toUpperCase(), this.value);
    }
}
