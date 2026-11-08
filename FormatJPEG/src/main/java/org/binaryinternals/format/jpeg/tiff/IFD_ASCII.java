/*
 * IFD_ASCII.java    Sep 09, 2010, 12:44
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

/// @author Amos Shi
public class IFD_ASCII extends IFD {

    public final String value;

    public IFD_ASCII(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, IFDType.ASCII, startPosTiff, byteArrayTiff);

        if (this.data_size > 0 && this.data_size <= 4) {
            this.value = pDIS.readASCII(4);                                    // The 4 bytes of the value field
        } else {
            super.ifd_value_offset = IFDParse.readInt(pDIS, byteOrder);             // Offset

            final PosDataInputStream reader = IFDParse.getTiffOffsetReader(byteArrayTiff, super.ifd_value_offset, super.data_size);
            this.value = reader.readASCII(super.ifd_count);
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.isValue()) {
            this.generateTreeNode_ASCII(
                    parentNode,
                    "%s: %s".formatted(super.getTagName(), this.value),
                    IFDMessage.getString(IFDMessage.KEY_IFD_Value));
        } else {
            this.generateTreeNode_ASCII(
                    parentNode,
                    "%s: %s".formatted(super.getTagName(), this.value),
                    IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
        }
    }

    void generateTreeNode_ASCII(DefaultMutableTreeNode parentNode, String node, String description) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        if (this.isValue()) {
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    node);
            comp.setDescription(description);
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    ShortText.getString(ShortText.KEY_Offset_n).formatted(super.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    super.data_size,
                    node,
                    Icons.Shortcut, null);
            comp.setDescription(description);
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }
}
