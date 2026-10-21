/*
 * IFD_LONG.java    Sep 09, 2010, 12:46
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.FileFormatException;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/**
 *
 * @author Amos Shi
 */
public class IFD_LONG extends IFD {

    public final long[] value;

    public IFD_LONG(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, IFDType.LONG, startPosTiff, byteArrayTiff);

        if (this.data_size > 0 && this.data_size <= 4) {
            this.value = new long[1];
            this.value[0] = IFDParse.readUnsignedInt(pDIS, byteOrder);
        } else {
            super.ifd_value_offset = IFDParse.readInt(pDIS, byteOrder);             // Offset

            final PosDataInputStream reader = IFDParse.getTiffOffsetReader(byteArrayTiff, super.ifd_value_offset, super.data_size);
            this.value = new long[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                this.value[i] = IFDParse.readUnsignedInt(reader, byteOrder);
            }
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.isValue()) {
            this.generateTreeNode_LONG(parentNode, IFDMessage.getString(IFDMessage.KEY_IFD_Value));
        }else{
            this.generateTreeNode_LONG(parentNode, IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
        }
    }

    public void generateTreeNode_LONG(DefaultMutableTreeNode parentNode, String description) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        if (this.isValue()) {
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    String.format("%s: %d", super.getTagName(), this.value[0]));
            comp.setDescription(description);
            parentNode.add(new DefaultMutableTreeNode(comp));
        } else {
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    String.format(ShortText.getString(ShortText.KEY_Offset_n), super.ifd_value_offset));
            comp.setDescription(description);
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    super.data_size,
                    super.getTagName(),
                    Icons.Shortcut, null);
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }
}
