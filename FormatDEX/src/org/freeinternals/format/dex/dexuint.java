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
 */
public class dexuint {
    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 4;
    
    /**
     * Value of the DEX <code>uint</code>.
     */
    public final long value;

    protected dexuint(long l) {
        this.value = l;
    }
}
