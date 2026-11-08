/*
 * IFD_8769_Exif.java    Sep 12, 2010, 22:40
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
public final class IFD_8769_Exif extends IFD_LONG_Pointer {

    public static final String CATEGORY_A = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_A);
    public static final String CATEGORY_B = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_B);
    public static final String CATEGORY_C = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_C);
    public static final String CATEGORY_D = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_D);
    public static final String CATEGORY_E = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_E);
    public static final String CATEGORY_F = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_F);
    public static final String CATEGORY_G = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G);

    public IFD_8769_Exif(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        IFD.checkIFDCount(super.ifd_tag, super.ifd_count, COUNT);
        this.parseSubIFDs(IFDParse::parse, TagSpace.DEFAULT);
    }

    public final long getExifOffset() {
        return super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        // Offset
        comp = new JTreeNodeFileComponent(
                pos + 8,
                4,
                "Exif Offset: %d".formatted(this.getExifOffset()));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));


        // Sub IFD
        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + (int) this.value[0],
                2 + this.ifd_number * IFD.SIZE,
                "Exif Sub IFD",
                Icons.Shortcut, null);
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD));
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(comp);
        parentNode.add(node);

        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + (int) this.value[0],
                2,
                "IFD number: %d".formatted(this.ifd_number));
        comp.setDescription(TIFF.IFD_Number_Description);
        node.add(new DefaultMutableTreeNode(comp));

        if (this.ifd_sub == null) {
            return;
        }

        // Sub IFD[]
        for (int i = 0; i < this.ifd_sub.length; i++) {
            comp = new JTreeNodeFileComponent(
                    this.ifd_sub[i].getStartPos(),
                    IFD.SIZE,
                    "Sub IFD[%d] - %s".formatted(i, this.ifd_sub[i].getTagName()));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD));
            final DefaultMutableTreeNode node_subifd = new DefaultMutableTreeNode(comp);
            node.add(node_subifd);
            if (this.ifd_sub[i] != null) {
                this.ifd_sub[i].generateTreeNode(node_subifd);
            }
        }

        // Next IFD
        super.generateTreeNode_NextIFD(node);
    }
    /// @see IFD_9000_ExifVersion
    /// @see IFD_A000_FlashpixVersion
    public static final String FormatVersion = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_FormatVersion);  // Section 4.2

    /// @see IFD_9000_ExifVersion
    /// @see IFD_A000_FlashpixVersion
    static String getVersion(byte[] value) {
        // Bytes A1 A2 . B1 B2
        return new String(value, 0, 2, StandardCharsets.ISO_8859_1) + '.' + new String(value, 2, 2, StandardCharsets.ISO_8859_1);
    }
}
