/*
 * Type_uint.java    June 17, 2015, 21:39
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

/**
 * 32-bit unsigned int, little-endian.
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
public class Type_uint {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 4;

    /**
     * Value of the DEX <code>uint</code>.
     */
    public final long value;

    protected Type_uint(long l) {
        this.value = l;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Type_uint && ((Type_uint)other).value == this.value;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.value).hashCode();
    }

    public int intValue() {
        return (int) value;
    }

    @Override
    public String toString() {
        return Type_uint.toString(this.value);
    }

    /**
     * Convert <code>long</code> to String with both offset and readable format.
     *
     * @param l The <code>long</code> value
     * @return The String for <code>l</code>
     */
    public static String toString(long l) {
        return String.format("0x%s | %,d", Long.toHexString(l).toUpperCase(), l);
    }
}
