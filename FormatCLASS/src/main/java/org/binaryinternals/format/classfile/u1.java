/*
 * u1.java    4:21 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.binaryinternals.commonlib.core.PosDataInputStream;

/**
 * An unsigned one-byte quantity.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressFBWarnings(value="NM_CLASS_NAMING_CONVENTION", justification="Use the type name from JVM Spec")
@SuppressWarnings("java:S101")
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
    public int intValue() {
        return this.value;
    }
}
