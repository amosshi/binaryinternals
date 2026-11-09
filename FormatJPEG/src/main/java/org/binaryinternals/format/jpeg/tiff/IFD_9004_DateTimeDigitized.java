/*
 * IFD_9004_DateTimeDigitized.java    Oct 16, 2010, 14:59
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0x9004`, `DateTimeDigitized`.
///
/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_F
public class IFD_9004_DateTimeDigitized extends IFD_ASCII {

    /// The expected number of values.
    public static final int COUNT = 20;
    /// The date and time, or `null` when the value is not a valid date.
    public final LocalDateTime datetime;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_9004_DateTimeDigitized(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
        this.datetime = IFD_0132_DateTime.parse(this.value);
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_ASCII(
                parentNode,
                IFD_0132_DateTime.toText(this.datetime, this.value),
                this.getDescription() + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_F));
    }
}
