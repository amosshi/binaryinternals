/*
 * IFD_A302_CFAPattern.java    Oct 28, 2010, 19:03
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
import org.freeinternals.biv.jpeg.ui.resource.ImageLoader;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 * @see IFD_8769_Exif#Category_G
 */
public class IFD_A302_CFAPattern extends IFD_UNDEFINED {

    public static final String[] FilterColor = {"RED", "GREEN", "BLUE", "CYAN", "MAGENTA", "YELLOW", "WHITE"};
    /** Horizontal repeat pixel unit. */
    public final int n;
    /** Vertical repeat pixel unit. */
    public final int m;

    public IFD_A302_CFAPattern(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
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
        DefaultMutableTreeNode node;
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
                    String.format(ShortText.getString(ShortText.KEY_Offset_n), super.ifd_value_offset));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
            parentNode.add(new DefaultMutableTreeNode(comp));

            comp = new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    super.data_size,
                    super.getTagName(),
                    ImageLoader.getShortcutIcon());
            comp.setDescription(description);
            parentNode.add(node = new DefaultMutableTreeNode(comp));

            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset,
                    2,
                    String.format("Horizontal repeat pixel unit = %d", this.n))));
            node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    super.tiff_StartPos + super.ifd_value_offset + 2,
                    2,
                    String.format("Vertical repeat pixel unit = %d", this.m))));

            int cfa;
            for (int i = 0; i < this.ifd_count - 4; i++) {
                cfa = (int) super.value[4 + i];
                node.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                        super.tiff_StartPos + super.ifd_value_offset + 4 + i,
                        1,
                        String.format("CFA value: %d - %s", cfa, FilterColor[cfa]))));
            }
        }
    }
}
