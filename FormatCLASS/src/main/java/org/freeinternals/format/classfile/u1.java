/*
 * u1.java    4:21 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class u1 {

    /**
     * Length of the {@link u1} component.
     */
    public static final int LENGTH = 1;

    /**
     * Value of the {@link u1} component.
     */
    public final short value;

    public u1(short value) {
        this.value = value;
    }

    public u1(final PosDataInputStream posDataInputStream) throws IOException {
        this.value = posDataInputStream.readByte();
    }

    public u1(final PosDataInputStream posDataInputStream, boolean unsigned) throws IOException {
        if (unsigned) {
            this.value = (short) posDataInputStream.readUnsignedByte();
        } else {
            this.value = posDataInputStream.readByte();
        }
    }

    /**
     * Return the {@link #value} as <code>int</code>.
     *
     * @return The {@link #value} as <code>int</code>
     */
    public int IntValue() {
        return new Short(value).intValue();
    }
}
