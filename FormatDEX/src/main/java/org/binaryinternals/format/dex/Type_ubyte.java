/*
 * Type_ubyte.java    June 17, 2015, 21:32
 *
 * Copyright 2015, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.dex;

/**
 * 8-bit unsigned int.
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
public class Type_ubyte {

    /**
     * Length of the type in bytes.
     */
    public static final int LENGTH = 1;

    /**
     * Value of the DEX <code>ubyte</code>.
     */
    public final int value;

    protected Type_ubyte(int i) {
        this.value = i;
    }

    @Override
    public String toString() {
        return String.format("0x%s | %d", Integer.toHexString(this.value).toUpperCase(), this.value);
    }

    /**
     * Get string for <code>ubyte</code> array.
     *
     * @param bytes <code>ubyte</code> array
     * @return String value
     */
    public static String toString(Type_ubyte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            return "empty array";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("array (");
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toHexString(bytes[i].value).toUpperCase());
            if (i != bytes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }
}
