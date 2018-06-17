/*
 * dexint.java    June 17, 2015, 21:38
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

/**
 * 32-bit signed int, little-endian.
 * 
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 */
public class Dex_int {
    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 4;
    
    /**
     * Value of the DEX <code>int</code>.
     */
    public final int value;

    protected Dex_int(int i) {
        this.value = i;
    }
}
