/*
 * dexbyte.java    June 17, 2015, 21:32
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
 */
public class Dex_byte {

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

    protected Dex_byte(byte b) {
        this.value = b;
    }
}
