/*
 * s15Fixed16Number.java    Nov 10, 2010, 09:18
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.icc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
@SuppressFBWarnings(value = "NM_CLASS_NAMING_CONVENTION", justification = "Use the type name from ICC Specification")
public class s15Fixed16Number {

    /// 2<sup>16</sup>, the scale of the 16 fractional bits.
    private static final double FRACTION_SCALE = 65536.0;

    public final byte[] rawData;
    public final double value;

    public s15Fixed16Number(final PosDataInputStream input) throws IOException {
        this.rawData = new byte[Integer.BYTES];
        input.readFully(this.rawData);

        // A signed 32-bit big-endian value, with 16 fractional bits
        this.value = ByteBuffer.wrap(this.rawData).getInt() / FRACTION_SCALE;
    }

    @Override
    public String toString() {
        return "%.4f".formatted(this.value);
    }
}
