/*
 * IFD_A210_FocalPlaneResolutionUnit.java    Oct 30, 2010, 18:12
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0xA210`, `FocalPlaneResolutionUnit`.
///
/// @author Amos Shi
/// @see IFD_A20E_FocalPlaneXResolution
/// @see IFD_A20F_FocalPlaneYResolution
/// @see IFD_8769_Exif#CATEGORY_G
public class IFD_A210_FocalPlaneResolutionUnit extends IFD_SHORT_COUNT1 {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_A210_FocalPlaneResolutionUnit(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(
                parentNode,
                this.getDescription()
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G));
    }
}
