/*
 * u1.java    4:21 AM, August 5, 2007
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
public class u1 {

    public static final short MIN_VALUE = 0;
    public static final short MAX_VALUE = 0xFF;

    /**
     * Value of the {@link u1} component.
     */
    public final short value;

    u1(short value) {
        this.value = value;
    }
    
    /**
     * Return the {@link #value} as <code>int</code>.
     * 
     * @return The {@link #value} as <code>int</code>
     */
    public int IntValue(){
        return new Short(value).intValue();
    }
}
