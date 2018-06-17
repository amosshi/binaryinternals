/*
 * u4.java    3:29 AM, August 5, 2007
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
public class u4 {

    /**
     * Length of the {@link u4} component.
     */
    public static final int LENGTH = 4;

    /**
     * Value of the {@link u4} component.
     */
    public int value;

    u4(final PosDataInputStream posDataInputStream) throws IOException {
        value = posDataInputStream.readInt();
    }
}
