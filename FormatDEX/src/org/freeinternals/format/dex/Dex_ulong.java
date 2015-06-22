/*
 * dexulong.java    June 17, 2015, 21:40
 *
 * Copyright 2015, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.dex;

import java.math.BigInteger;

/**
 * 64-bit unsigned int, little-endian.
 *
 * @author Amos Shi
 * @see
 * <a href="https://source.android.com/devices/tech/dalvik/dex-format.html">
 * Dalvik Executable (DEX) format</a>
 */
public class Dex_ulong {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 8;

    /**
     * Value of the DEX <code>ulong</code>.
     */
    public final BigInteger value;

    protected Dex_ulong(BigInteger bi) {
        this.value = bi;
    }
}