/*
 * IFD_A302_CFAPattern.java    Oct 28, 2010, 19:03
 *
 * Copyright 2010, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.binaryinternals.commonlib.Icons;
import org.binaryinternals.commonlib.JTreeNodeFileComponent;
import org.binaryinternals.commonlib.PosByteArrayInputStream;
import org.binaryinternals.commonlib.PosDataInputStream;

/// @author Amos Shi
/// @see IFD_8769_Exif#CATEGORY_G
public class IFD_A302_CFAPattern extends IFD_UNDEFINED {

    static final String[] FILTER_COLORS = {"RED", "GREEN", "BLUE", "CYAN", "MAGENTA", "YELLOW", "WHITE"};
    /// Horizontal repeat pixel unit.
    public final int n;
    /// Vertical repeat pixel unit.
    public final int m;

    public IFD_A302_CFAPattern(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        final PosDataInputStream reader = new PosDataInputStream(new PosByteArrayInputStream(super.value));
        n = reader.readUnsignedShort();
        m = reader.readUnsignedShort();
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;
        String description = IFDMessage.getString(IFDMessage.KEY_IFD_A302_Description)
                    + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G);

        if (this.isValue()) {
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    super.getTagName());
            comp.setDescription(description);
            parentNode.add(new DefaultMutableTreeNode(comp));

        } else {
            // Offset
            comp = new JTreeNodeFileComponent(
                    pos + 8,
                    4,
                    ShortText.getString(ShortText.KEY_Offset_n).formatted(super.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    super.data_size,
                    super.getTagName(),
                    Icons.Shortcut, null);
            comp.setDescription(description);
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
            parentNode.add(node);

            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    2,
                    "Horizontal repeat pixel unit = %d".formatted(this.n))));
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + 2,
                    2,
                    "Vertical repeat pixel unit = %d".formatted(this.m))));

            for (int i = 0; i < this.ifd_count - 4; i++) {
                final int cfa = (int) super.value[4 + i];
                node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        super.tiff_StartPos + super.ifd_value_offset + 4 + i,
                        1,
                        "CFA value: %d - %s".formatted(cfa, FILTER_COLORS[cfa]))));
            }
        }
    }
}
