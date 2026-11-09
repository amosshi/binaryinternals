/*
 * IFD_A405_FocalLengthIn35mmFilm.java    Oct 28, 2010, 23:30
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0xA405`, `FocalLengthIn35mmFilm`.
///
/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_G
/// @see IFD_920A_FocalLength
public class IFD_A405_FocalLengthIn35mmFilm extends IFD_SHORT_COUNT1 {

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_A405_FocalLengthIn35mmFilm(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(
                parentNode,
                this.getDescription()
                + IFD_8769_Exif.CATEGORY_G);
    }
}
