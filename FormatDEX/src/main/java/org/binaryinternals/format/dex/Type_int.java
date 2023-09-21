/*
 * Type_int.java    June 17, 2015, 21:38
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

/**
 * 32-bit signed int, little-endian.
 *
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We need different Name convesins for readability
 * </pre>
 */
@SuppressWarnings("java:S101")
public class Type_int {
    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 4;

    /**
     * Value of the DEX <code>int</code>.
     */
    public final int value;

    protected Type_int(int i) {
        this.value = i;
    }

    @Override
    public String toString() {
        return String.format("0x%s | %,d", Integer.toHexString(this.value).toUpperCase(), this.value);
    }
}
