/*
 * IFD_9000_ExifVersion.java    Oct 16, 2010, 16:24
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0x9000`, `ExifVersion`.
///
/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_A
/// @see IFD_8769_Exif#FormatVersion
public class IFD_9000_ExifVersion extends IFD_UNDEFINED {

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
    public IFD_9000_ExifVersion(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    @Override
    public boolean isValue() {
        return true;
    }

    /// Gets the Exif version, like `2.32`.
    ///
    /// @return The version
    public String getExifVersion() {
        return IFD_8769_Exif.getVersion(super.value);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                "%s: %s".formatted(this.getTagName(), this.getExifVersion()),
                this.getDescription()
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_A)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_FormatVersion));
    }
}
