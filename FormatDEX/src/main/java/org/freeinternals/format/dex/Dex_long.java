/*
 * dexlong.java    June 17, 2015, 21:39
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

/**
 * 64-bit signed int, little-endian.
 *
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 */
public class Dex_long {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 8;
    /**
     * Value of the DEX <code>long</code>.
     */
    public final long value;

    protected Dex_long(long l) {
        this.value = l;
    }
}
