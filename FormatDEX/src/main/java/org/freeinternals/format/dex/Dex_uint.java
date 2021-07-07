/*
 * dexuint.java    June 17, 2015, 21:39
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

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
public class Dex_uint {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 4;

    /**
     * Value of the DEX <code>uint</code>.
     */
    public final long value;

    protected Dex_uint(long l) {
        this.value = l;
    }

    public int intValue() {
        return Long.valueOf(this.value).intValue();
    }

    @Override
    public String toString() {
        return Dex_uint.toString(this.value);
    }

    /**
     * Convert <code>long</code> to String with both offset and readable format.
     * 
     * @param l The <code>long</code> value
     * @return The String for <code>l</code>
     */
    public static String toString(long l) {
        return "0x" + Long.toHexString(l).toUpperCase() + " | " + String.format("%,d", l);
    }
}
