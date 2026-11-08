/*
 * Type_ubyte.java    June 17, 2015, 21:32
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.dex;

import java.util.Arrays;
import java.util.stream.Collectors;

/// 8-bit unsigned int.
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
public final class Type_ubyte {

    /// Length of the type in bytes.
    public static final int LENGTH = 1;

    /// Value of the DEX `ubyte`.
    public final int value;

    Type_ubyte(int i) {
        this.value = i;
    }

    @Override
    public String toString() {
        return "0x%X | %d".formatted(this.value, this.value);
    }

    /// Get string for `ubyte` array.
    ///
    /// @param bytes `ubyte` array
    /// @return String value
    public static String toString(Type_ubyte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            return "empty array";
        }

        return Arrays.stream(bytes)
                .map(b -> "%X".formatted(b.value))
                .collect(Collectors.joining(", ", "array (", ")"));
    }
}
