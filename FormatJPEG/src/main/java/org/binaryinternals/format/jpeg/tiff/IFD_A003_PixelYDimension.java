/*
 * IFD_A003_PixelYDimension.java    Oct 28, 2010, 13:06
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0xA003`, `PixelYDimension`.
///
/// @author Amos Shi
/// @see IFD_SHORT
/// @see IFD_LONG
/// @see IFD_8769_Exif#CATEGORY_C
public class IFD_A003_PixelYDimension extends IFD_LONG_COUNT1 {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_A003_PixelYDimension(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_LONG(
                parentNode,
                this.getDescription()
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_C));
    }
}
