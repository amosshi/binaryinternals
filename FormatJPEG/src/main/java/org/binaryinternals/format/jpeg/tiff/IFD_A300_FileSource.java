/*
 * IFD_A300_FileSource.java    Oct 28, 2010, 13:31
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0xA300`, `FileSource`.
///
/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_G
public class IFD_A300_FileSource extends IFD_UNDEFINED {

    /// The expected number of values.
    public static final int COUNT = 1;

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_A300_FileSource(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    @Override
    public boolean isValue() {
        return true;
    }

    /// Gets the image source, `3` for a digital still camera.
    ///
    /// @return The file source value
    public int getFileSource() {
        return (int) super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                "%s: %d".formatted(this.getTagName(), this.getFileSource()),
                this.getDescription()
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G));
    }
}
