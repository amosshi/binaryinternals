/*
 * IFD_9286_UserComment.java    Oct 27, 2010, 09:19
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
 * @see IFD_010E_ImageDescription
 * @see IFD_8769_Exif#Category_D
 */
public class IFD_9286_UserComment extends IFD_UNDEFINED {

    public final String CharacterCode;

    public IFD_9286_UserComment(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        if (super.value.length >= 8) {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                if (super.value[i] == 0) {
                    break;
                }
                sb.append((char)super.value[i]);
            }
            this.CharacterCode = sb.toString();
        } else {
            this.CharacterCode = null;
        }
    }

    @Override
    public boolean isValue() {
        return false;
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;
        DefaultMutableTreeNode node;

        comp = new JTreeNodeFileComponent(
                pos + 8,
                4,
                String.format(ShortText.getString(ShortText.KEY_Offset_n), super.ifd_value_offset));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + super.ifd_value_offset,
                super.data_size,
                this.getTagName(),
                ImageLoader.getShortcutIcon());
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_9286_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_D));
        parentNode.add(node = new DefaultMutableTreeNode(comp));

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.tiff_StartPos + super.ifd_value_offset,
                8,
                String.format("Character Code: %s", this.CharacterCode))));
        if (super.data_size - 8 > 0) {
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + 8,
                    super.data_size - 8,
                    this.getTagName())));
        }
    }
}
