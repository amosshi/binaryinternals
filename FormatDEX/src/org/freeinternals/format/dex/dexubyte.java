/*
 * dexubyte.java    June 17, 2015, 21:32
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

/**
 * 8-bit unsigned int.
 *
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 */
public class dexubyte {
    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 1;

    /**
     * Value of the DEX <code>ubyte</code>.
     */
    public final int value;

    protected dexubyte(int i) {
        this.value = i;
    }
}
