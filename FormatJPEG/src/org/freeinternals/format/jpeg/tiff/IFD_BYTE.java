/*
 * IFD_BYTE.java    Sep 09, 2010, 12:44
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.biv.jpeg.ui.resource.ImageLoader;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 */
public class IFD_BYTE extends IFD {

    public final byte[] value;

    public IFD_BYTE(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, IFDType.ASCII, startPosTiff, byteArrayTiff);

        if (super.isValue()) {
            this.value = new byte[4];
            this.value[0] = pDIS.readByte();
            this.value[1] = pDIS.readByte();
            this.value[2] = pDIS.readByte();
            this.value[3] = pDIS.readByte();
        } else {
            super.ifd_value_offset = super.readInt(pDIS);                           // Offset

            final PosDataInputStream reader = super.getTiffOffsetReader();
            this.value = new byte[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                this.value[i] = reader.readByte();
            }
        }

    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.isValue()) {
            this.generateTreeNode_BYTE(
                    parentNode,
                    String.format("%s: %s", super.getTagName(), this.value),
                    IFDMessage.getString(IFDMessage.KEY_IFD_Value));
        } else {
            this.generateTreeNode_BYTE(
                    parentNode,
                    String.format("%s: %s", super.getTagName(), this.value),
                    IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
        }
    }

    // The same as ASCII
    void generateTreeNode_BYTE(DefaultMutableTreeNode parentNode, String node, String description) {
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
                    String.format(ShortText.getString(ShortText.KEY_Offset_n), super.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    super.data_size,
                    node,
                    ImageLoader.getShortcutIcon());
            comp.setDescription(description);
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }

}
