/*
 * IFD_0201_JPEGInterchangeFormat.java    Oct 31, 2010, 23:14
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// TIFF 6.0 tag `0x0201`, `JPEGInterchangeFormat`.
///
/// @author Amos Shi
public class IFD_0201_JPEGInterchangeFormat extends IFD_LONG_COUNT1 {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_0201_JPEGInterchangeFormat(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /// Whether a JPEG compressed thumbnail is present.
    ///
    /// @return `true` if the offset to the thumbnail is not `0`
    public boolean isBitstreamPresent() {
        return super.value[0] > 0;
    }

    /// The `JPEG` interchange format bit stream offset within the `TIFF`.
    /// This attribute is valid only if [#isBitstreamPresent] returns `true`.
    /// @return the offset value
    public long getBitstreamOffset() {
        return super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_LONG(
                parentNode,
                this.getDescription());
    }
}
