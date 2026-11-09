/*
 * s15Fixed16Number.java    Nov 10, 2010, 09:18
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.icc;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.binaryinternals.commonlib.PosDataInputStream;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/// The type `s15Fixed16Number`: a fixed signed 4-byte number with 16 fractional bits.
///
/// @author Amos Shi
@SuppressFBWarnings(value = "NM_CLASS_NAMING_CONVENTION", justification = "Use the type name from ICC Specification")
public class s15Fixed16Number {

    /// 2<sup>16</sup>, the scale of the 16 fractional bits.
    private static final double FRACTION_SCALE = 65536.0;

    /// The raw 4 bytes.
    public final byte[] rawData;
    /// The value.
    public final double value;

    /// Reads the value.
    ///
    /// @param input Input stream of the the ICC profile
    /// @throws IOException if the stream cannot be read
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
