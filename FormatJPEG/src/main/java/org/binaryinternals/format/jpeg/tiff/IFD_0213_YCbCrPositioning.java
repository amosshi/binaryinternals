/*
 * IFD_0213_YCbCrPositioning.java    Sep 11, 2010, 23:48
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// TIFF 6.0 tag `0x0213`, `YCbCrPositioning`.
///
/// @author Amos Shi
public class IFD_0213_YCbCrPositioning extends IFD_SHORT_COUNT1 {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_0213_YCbCrPositioning(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {

        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    /// Gets the position of the chrominance components: `1` centered, `2` co-sited.
    ///
    /// @return The positioning value
    public int getYCbCrPositioning() {
        return super.getValue();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(
                parentNode,
                this.getDescription());
    }
}
