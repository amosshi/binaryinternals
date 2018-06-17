/*
 * u2.java    3:32 AM, August 5, 2007
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
public class u2 {

    /**
     * Length of the {@link u2} component.
     */
    public static final int LENGTH = 2;

    /**
     * Value of the {@link u2} component.
     */
    public int value;

    u2(final PosDataInputStream posDataInputStream) throws IOException {
        this.value = posDataInputStream.readUnsignedShort();
    }
}
