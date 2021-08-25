/*
 * Type_byte.java    June 17, 2015, 21:32
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

/**
 * 8-bit signed int.
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
public class Type_byte {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 1;

    /**
     * Value of the DEX <code>byte</code>.
     *
     * @see
     * <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2.1">
     * Java byte type</a>
     */
    public final byte value;

    protected Type_byte(byte b) {
        this.value = b;
    }

    @Override
    public String toString() {
        return String.format("0x%s | %d | %c", Integer.toHexString(this.value).toUpperCase(), this.value, this.value);
    }
}
