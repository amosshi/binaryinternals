/*
 * IFD_8825_GPS.java    Oct 01, 2010, 10:10
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// Exif tag `0x8825`, `GPS`.
///
/// @author Amos Shi
public final class IFD_8825_GPS extends IFD_LONG_Pointer {

    /// Category A: the GPS attribute information tags.
    public static final String Category_A = IFDMessage.getString(IFDMessage.KEY_IFD_8825_GPS_Category_A);

    /// Reads the IFD entry, with its tag and type already read.
    ///
    /// @param pDIS The input stream, positioned after the type
    /// @param byteOrder Byte order of the TIFF data
    /// @param tag The tag
    /// @param startPosTiff Start position of the TIFF header in the file
    /// @param byteArrayTiff The TIFF data, from the TIFF header
    /// @throws IOException if the entry cannot be read
    public IFD_8825_GPS(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        this.parseSubIFDs(IFDParse::parseGPS, TagSpace.GPS);
    }

    /// Gets the offset of the GPS IFD.
    ///
    /// @return The offset from the TIFF header
    public long getGPSOffset() {
        return super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                pos + 8,
                4,
                "GPS Offset: %d".formatted(super.value[0]));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));

        // Sub IFD

        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + (int) this.value[0],
                2 + this.ifd_number * IFD.SIZE,
                "GPS Sub IFD",
                Icons.Shortcut, null);
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD));
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
        parentNode.add(node);

        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + (int) this.value[0],
                2,
                "IFD number: %d".formatted(this.ifd_number));
        comp.setDescription(TIFF.IFD_Number_Description);
        node.add(new DefaultMutableTreeNode(comp));

        if (this.ifd_sub == null) {
            return;
        }

        // Sub IFD[]
        for (int i = 0; i < this.ifd_sub.length; i++) {
            comp = new JTreeNodeFileComponent(
                    this.ifd_sub[i].getStartPos(),
                    IFD.SIZE,
                    "Sub IFD[%d] - %s".formatted(i, this.ifd_sub[i].getTagName()));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD));
            final DefaultMutableTreeNode node_subifd = new DefaultMutableTreeNode(comp);
            node.add(node_subifd);
            if (this.ifd_sub[i] != null) {
                this.ifd_sub[i].generateTreeNode(node_subifd);
            }
        }

        // Next IFD
        super.generateTreeNode_NextIFD(node);
    }
}
