/*
 * IFD_SHORT.java    Sep 09, 2010, 12:46
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
public class IFD_SHORT extends IFD {

    public final int[] value;

    public IFD_SHORT(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff) 
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, IFDType.SHORT, startPosTiff, byteArrayTiff);

        if (super.isValue()) {
            this.value = new int[2];
            this.value[0] = super.readUnsignedShort(pDIS);
            this.value[1] = super.readUnsignedShort(pDIS);
        } else {
            super.ifd_value_offset = super.readInt(pDIS);                           // Offset

            final PosDataInputStream reader = super.getTiffOffsetReader();
            this.value = new int[super.ifd_count];
            for (int i = 0; i < super.ifd_count; i++) {
                this.value[i] = super.readUnsignedShort(reader);
            }
        }
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        if (this.isValue()) {
            this.generateTreeNode_SHORT(parentNode, IFDMessage.getString(IFDMessage.KEY_IFD_Value));
        }else{
            this.generateTreeNode_SHORT(parentNode, IFDMessage.getString(IFDMessage.KEY_IFD_Value_Ref));
        }
    }

    /**
     * 
     * @param parentNode
     * @param description 
     */
    void generateTreeNode_SHORT(DefaultMutableTreeNode parentNode, String description) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        if (this.isValue()) {
            switch (super.ifd_count) {
                case 1:
                    comp = new JTreeNodeFileComponent(
                            pos + 8,
                            2,
                            String.format("%s: %d", super.getTagName(), this.value[0]));
                    comp.setDescription(description);
                    parentNode.add(new DefaultMutableTreeNode(comp));

                    parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                            pos + 10,
                            2,
                            ShortText.getString(ShortText.KEY_Unused))));
                    break;
                case 2:
                    comp = new JTreeNodeFileComponent(
                            pos + 8,
                            2,
                            String.format("%s[0]: %d", super.getTagName(), this.value[0]));
                    comp.setDescription(description);
                    parentNode.add(new DefaultMutableTreeNode(comp));

                    comp = new JTreeNodeFileComponent(
                            pos + 10,
                            2,
                            String.format("%s[1]: %d", super.getTagName(), this.value[1]));
                    comp.setDescription(description);
                    parentNode.add(new DefaultMutableTreeNode(comp));
                    break;
            }
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
                    super.getTagName(),
                    ImageLoader.getShortcutIcon());
            comp.setDescription(description);
            parentNode.add(new DefaultMutableTreeNode(comp));
        }
    }
}
