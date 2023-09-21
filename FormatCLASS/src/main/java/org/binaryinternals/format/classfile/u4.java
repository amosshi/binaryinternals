/*
 * u4.java    3:29 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.binaryinternals.commonlib.core.PosDataInputStream;

/**
 * An unsigned four-byte quantity.
 *
 * @author Amos Shi
 *
 * <pre>
 * java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
 * </pre>
 */
@SuppressFBWarnings(value="NM_CLASS_NAMING_CONVENTION", justification="Use the type name from JVM Spec")
@SuppressWarnings("java:S101")
public class u4 {

    /**
     * Length of the {@link u4} component.
     */
    public static final int LENGTH = 4;

    /**
     * Value of the {@link u4} component.
     */
    public final int value;

    public u4(final PosDataInputStream posDataInputStream) throws IOException {
        value = posDataInputStream.readInt();
    }
}
