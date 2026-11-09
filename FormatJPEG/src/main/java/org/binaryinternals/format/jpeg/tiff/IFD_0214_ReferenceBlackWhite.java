/*
 * IFD_0214_ReferenceBlackWhite.java    Oct 04, 2010, 11:08
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// TIFF 6.0 tag `0x0214`, `ReferenceBlackWhite`.
///
/// @author Amos Shi
/// @see IFD_0213_YCbCrPositioning
/// @see org.binaryinternals.format.jpeg.ps.PhotoshopImageResource
public class IFD_0214_ReferenceBlackWhite extends IFD_RATIONAL {

    /// The expected number of values.
    public static final int COUNT = 6;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_0214_ReferenceBlackWhite(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    /// {@inheritDoc}
    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_RATIONAL(parentNode, this.getDescription());
    }
}
