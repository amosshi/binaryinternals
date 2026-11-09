/*
 * IFD_9101_ComponentsConfiguration.java    Oct 16, 2010, 15:22
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0x9101`, `ComponentsConfiguration`.
///
/// @author Amos Shi
/// @see IFD_0106_PhotometricInterpretation
/// @see IFD_8769_Exif#CATEGORY_C
public class IFD_9101_ComponentsConfiguration extends IFD_UNDEFINED {

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
    public IFD_9101_ComponentsConfiguration(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    @Override
    public boolean isValue() {
        return true;
    }

    /// Gets the channels of each component, like `1 2 3 0` for YCbCr.
    ///
    /// @return The components text
    public String getValue() {
        final StringBuilder sb = new StringBuilder(20);
        sb.append(super.value[0]);
        sb.append(' ');
        sb.append(super.value[1]);
        sb.append(' ');
        sb.append(super.value[2]);
        sb.append(' ');
        sb.append(super.value[3]);

        return sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_UNDEFINED(
                parentNode,
                "%s: %s".formatted(this.getTagName(), this.getValue()),
                this.getDescription()
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_C));
    }
}
