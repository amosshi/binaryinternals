/*
 * IFD_RATIONAL.java    Oct 16, 2010, 12:49
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import org.binaryinternals.commonlib.PosDataInputStream;

/// An IFD entry of type `RATIONAL`, with exactly one unsigned fraction.
///
/// @author Amos Shi
public class IFD_RATIONAL_COUNT1 extends IFD_RATIONAL {

    /// The expected number of values.
    public static final int COUNT = 1;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_RATIONAL_COUNT1(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {

        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    @Override
    public boolean isValue() {
        return false;
    }

    /// Gets the value.
    ///
    /// @return The value
    public Rational getValue() {
        return this.value[0];
    }
}
