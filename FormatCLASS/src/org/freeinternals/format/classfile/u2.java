/*
 * u2.java    3:32 AM, August 5, 2007
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
public class u2 {

    /**
     * Length of the {@link u2} component.
     */
    public static final int LENGTH = 2;
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 0xFFFF;
    /**
     * Value of the {@link u2} component.
     */
    public int value;

    u2(int value) {
        this.value = value;
    }
}
