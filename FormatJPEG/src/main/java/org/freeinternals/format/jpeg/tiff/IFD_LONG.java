/*
 * IFD_LONG.java    Sep 09, 2010, 12:46
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
public class IFD_LONG extends IFD {

    public final long[] value;

    public IFD_LONG(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, IFDType.LONG, startPosTiff, byteArrayTiff);

        if (super.isValue()) {
            this.value = new long[1];
            this.value[0] = super.readUnsignedInt(pDIS);
        } else {
            super.ifd_value_offset = super.readInt(pDIS);                           // Offset

            final PosDataInputStream reader = super.getTiffOffsetReader();
            this.value = new long[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                this.value[i] = super.readUnsignedInt(reader);
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
                    ImageLoader.getShortcutIcon());
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }
}
