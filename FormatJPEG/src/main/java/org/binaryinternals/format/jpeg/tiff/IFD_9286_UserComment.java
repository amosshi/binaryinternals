/*
 * IFD_9286_UserComment.java    Oct 27, 2010, 09:19
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
/// @see IFD_010E_ImageDescription
/// @see IFD_8769_Exif#CATEGORY_D
public class IFD_9286_UserComment extends IFD_UNDEFINED {

    public final String CharacterCode;

    public IFD_9286_UserComment(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        if (super.value.length >= 8) {
            // The 8-byte character code, ended by the first NUL
            int end = 0;
            while (end < 8 && super.value[end] != 0) {
                end++;
            }
            this.CharacterCode = new String(super.value, 0, end, StandardCharsets.ISO_8859_1);
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

        comp = new JTreeNodeFileComponent(
                pos + 8,
                4,
                ShortText.getString(ShortText.KEY_Offset_n).formatted(super.ifd_value_offset));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));

        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + super.ifd_value_offset,
                super.data_size,
                this.getTagName(),
                Icons.Shortcut, null);
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_9286_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_D));
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
        parentNode.add(node);

        node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.tiff_StartPos + super.ifd_value_offset,
                8,
                "Character Code: %s".formatted(this.CharacterCode))));
        if (super.data_size - 8 > 0) {
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + 8,
                    super.data_size - 8,
                    this.getTagName())));
        }
    }
}
