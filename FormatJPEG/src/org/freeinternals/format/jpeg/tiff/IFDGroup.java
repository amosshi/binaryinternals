/*
 * IFDGroup.java    Oct 31, 2010, 19:04
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosByteArrayInputStream;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.FileFormatException;

class IFDGroup {

    /** Absolute start position within the file. */
    public final int startPos;
    /** Total length of the group, in bytes. */
    public final int length;
    /** The Offset position within TIFF. */
    public final int offset;
    /** The 2-byte count of the number of directory entries. */
    public final int ifd_number;
    /** The sequence of 12-byte IFD field entries. */
    public final IFD[] ifd;
    /** The 4-byte offset of the next IFD */
    public final int next;

    IFDGroup(byte[] byteArrayTiff, final TIFFHeader tiffHeader, final int offset)
            throws IOException, FileFormatException {

        // Absolute start position & Offset within tiff
        this.startPos = tiffHeader.getStartPos() + offset;
        this.offset = offset;

        // IFD count and data
        final PosDataInputStream pDisTiff = new PosDataInputStream(
                new PosByteArrayInputStream(byteArrayTiff),
                tiffHeader.getStartPos());
        pDisTiff.skip(offset);
        this.ifd_number = IFDParse.readUnsignedShort(pDisTiff, tiffHeader.byte_order);
        if (this.ifd_number > 0) {                                              // IFD count could be zero
            this.ifd = new IFD[this.ifd_number];
            for (int i = 0; i < this.ifd_number; i++) {
                this.ifd[i] = IFDParse.parse(pDisTiff, tiffHeader.byte_order, tiffHeader.getStartPos(), byteArrayTiff);
            }
        } else {
            this.ifd = null;
        }
        this.next = IFDParse.readInt(pDisTiff, tiffHeader.byte_order);

        // Size
        this.length = 2 + this.ifd_number * IFD.SIZE + 4;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode nodeGroup;
        DefaultMutableTreeNode nodeIFD;

        parentNode.add(nodeGroup = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos,
                this.length,
                "IFD Group")));

        // IFD number
        comp = new JTreeNodeFileComponent(
                this.startPos,
                2,
                String.format("IFD number: %d", this.ifd_number));
        comp.setDescription(TIFF.IFD_Number_Description);
        nodeGroup.add(new DefaultMutableTreeNode(comp));

        // IFD[]
        for (int i = 0; i < this.ifd_number; i++) {
            comp = new JTreeNodeFileComponent(
                    this.ifd[i].getStartPos(),
                    IFD.SIZE,
                    String.format("IFD[%d] - %s", i, this.ifd[i].getTagName()));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD));
            nodeGroup.add(nodeIFD = new DefaultMutableTreeNode(comp));
            this.ifd[i].generateTreeNode(nodeIFD);
        }

        // Next IFD
        nodeGroup.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                this.startPos + this.length - 4,
                4,
                String.format("Next IFD: Offset within Tiff = %04X (%d)", this.next, this.next))));
    }
}
