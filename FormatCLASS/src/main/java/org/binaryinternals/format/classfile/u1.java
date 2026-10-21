/*
 * u1.java    4:21 AM, August 5, 2007
 *
 * Copyright  2007, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.classfile;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.binaryinternals.commonlib.PosDataInputStream;

/// An unsigned one-byte quantity.
///
/// @author Amos Shi
///
/// ```
/// java:S101 - Class names should comply with a naming convention --- We respect the name from JVM Spec instead
/// ```
@SuppressFBWarnings(value="NM_CLASS_NAMING_CONVENTION", justification="Use the type name from JVM Spec")
@SuppressWarnings("java:S101")
public class u1 {

    /// Length of the {@link u1} component.
    public static final int LENGTH = 1;

    /// Value of the {@link u1} component.
    public final short value;

    public u1(short value) {
        this.value = value;
    }

    /// Reads one unsigned byte, as all `u1` items in the JVM Spec are
    /// unsigned.
    ///
    /// @param posDataInputStream The class file stream
    /// @throws IOException Failed to read the byte
    public u1(final PosDataInputStream posDataInputStream) throws IOException {
        this.value = (short) posDataInputStream.readUnsignedByte();
    }

    /// Return the {@link #value} as `int`.
    ///
    /// @return The {@link #value} as `int`
    public int intValue() {
        return this.value;
    }
}
