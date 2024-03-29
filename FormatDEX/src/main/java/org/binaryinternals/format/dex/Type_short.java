/*
 * Type_short.java    June 17, 2015, 21:36
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

/**
 * 16-bit signed int, little-endian.
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
public class Type_short {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 2;

    /**
     * Value of the DEX <code>short</code>.
     */
    public final short value;

    protected Type_short(short s) {
        this.value = s;
    }

    @Override
    public String toString() {
        return String.format("0x%s | %,d", Integer.toHexString(this.value).toUpperCase(), this.value);
    }
}
