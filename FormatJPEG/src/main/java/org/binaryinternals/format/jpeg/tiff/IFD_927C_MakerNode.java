/*
 * IFD_927C_MakerNode.java    Oct 27, 2010, 00:15
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0x927C`, `MakerNode`.
///
/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_D
public class IFD_927C_MakerNode extends IFD_UNDEFINED {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_927C_MakerNode(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                this.getTagName(),
                this.getDescription()
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_D));
    }
}
