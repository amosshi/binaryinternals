/*
 * dexuleb128p1.java    June 17, 2015, 21:42
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

/**
 * Unsigned LEB128 plus 1, variable-length.
 * 
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 */
public class Dex_uleb128p1 {

    /**
     * Value of the DEX <code>uleb128</code>.
     */
    public final int value;
    /**
     * Length of the number in DEX file in bytes.
     */
    public final int length;

    protected Dex_uleb128p1(int v, int l) {
        this.value = v;
        this.length = l;
    }
}
