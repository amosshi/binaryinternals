/*
 * IFD_0001_InteroperabilityIndex.java    Oct 30, 2010, 12:09
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Interoperability tag `0x0001`, `InteroperabilityIndex`.
///
/// @author Amos Shi
/// @see IFD_A005_Interoperability#Category_A
public class IFD_0001_InteroperabilityIndex extends IFD_ASCII {

    /// The expected number of values.
    public static final int COUNT = 4;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_0001_InteroperabilityIndex(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_ASCII(
                parentNode,
                "%s: %s".formatted(this.getTagName(), super.value),
                IFDMessage.getString(IFDMessage.KEY_IFD_ItO_0001_Description)
                + IFD_A005_Interoperability.Category_A);
    }
}
