/*
 * IFD_0000_GPSVersionID.java    Oct 30, 2010, 12:09
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.PosDataInputStream;

/// GPS tag `0x0000`, `GPSVersionID`.
///
/// @author Amos Shi
/// @see IFD_8825_GPS#Category_A
public class IFD_0000_GPSVersionID extends IFD_BYTE {

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
    public IFD_0000_GPSVersionID(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
    }

    @Override
    public boolean isValue() {
        return true;
    }

    /// Gets the GPS version, like `2.2.0.0`.
    ///
    /// @return The version
    public String getGPSVersionID() {
        StringBuilder sb = new StringBuilder(3);
        sb.append(super.value[0]);
        sb.append('.');
        sb.append(super.value[1]);
        return sb.toString();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_BYTE(
                parentNode,
                "%s: %s".formatted(this.getTagName(), this.getGPSVersionID()),
                IFDMessage.getString(IFDMessage.KEY_IFD_GPS_0000_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8825_GPS_Category_A));
    }
}
