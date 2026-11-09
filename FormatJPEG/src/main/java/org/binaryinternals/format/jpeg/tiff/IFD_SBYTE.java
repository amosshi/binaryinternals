/*
 * IFD_SBYTE.java    Sep 09, 2010, 12:47
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import org.binaryinternals.commonlib.PosDataInputStream;

/// An IFD entry of type `SBYTE`, 8-bit signed integers.
///
/// @author Amos Shi
public class IFD_SBYTE extends IFD {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_SBYTE(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, IFDType.ASCII, startPosTiff, byteArrayTiff);
    }
}
