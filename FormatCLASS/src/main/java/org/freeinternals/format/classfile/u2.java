/*
 * u2.java    3:32 AM, August 5, 2007
 *
 * Copyright  2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.classfile;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.freeinternals.commonlib.core.PosDataInputStream;

/**
 * An unsigned two-byte quantity.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressFBWarnings(value="NM_CLASS_NAMING_CONVENTION", justification="Use the type name from JVM Spec")
@SuppressWarnings("java:S101")
public class u2 {

    /**
     * Length of the {@link u2} component.
     */
    public static final int LENGTH = 2;

    /**
     * Value of the {@link u2} component.
     */
    public final int value;

    public u2(final PosDataInputStream posDataInputStream) throws IOException {
        this.value = posDataInputStream.readUnsignedShort();
    }
}
