/*
 * u4.java    3:29 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class u4 {

    static final int MIN_VALUE = 0;
    static final int MAX_VALUE = 0xFFFFFFFF;

    /**
     * Value of the {@link u4} component.
     */
    public int value;

    /**
     * Creates a new instance of u4
     *
     * @param v value of the {@link u4}
     */
    public u4(final int v) {
        value = v;
    }
}
