/*
 * IFD_0202_JPEGInterchangeFormatLength.java    Oct 31, 2010, 23:14
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// TIFF 6.0 tag `0x0202`, `JPEGInterchangeFormatLength`.
///
/// @author Amos Shi
/// @see IFD_0201_JPEGInterchangeFormat
public class IFD_0202_JPEGInterchangeFormatLength extends IFD_LONG_COUNT1 {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_0202_JPEGInterchangeFormatLength(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /// The length in bytes of the `JPEG` interchange format bit stream.
    ///
    /// @return the length
    public long getBitstreamLength() {
        return super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_LONG(
                parentNode,
                this.getDescription());
    }
}
