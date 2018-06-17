/*
 * dexushort.java    June 17, 2015, 21:37
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

/**
 * 16-bit unsigned int, little-endian.
 *
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 */
public class Dex_ushort {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 2;

    /**
     * Value of the DEX <code>ubyte</code>.
     */
    public final int value;

    protected Dex_ushort(int i) {
        this.value = i;
    }
    
    @Override
    public String toString() {
        return "0x" + Long.toHexString(this.value).toUpperCase() + " | " + String.format("%,d", this.value);
    }    
}
