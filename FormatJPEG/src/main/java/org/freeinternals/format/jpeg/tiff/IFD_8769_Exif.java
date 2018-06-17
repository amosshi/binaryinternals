/*
 * IFD_8769_Exif.java    Sep 12, 2010, 22:40
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
 */
public class IFD_8769_Exif extends IFD_LONG_Pointer {

    public static final String Category_A = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_A);
    public static final String Category_B = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_B);
    public static final String Category_C = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_C);
    public static final String Category_D = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_D);
    public static final String Category_E = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_E);
    public static final String Category_F = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_F);
    public static final String Category_G = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_G);

    public IFD_8769_Exif(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
        super.checkIFDCount(COUNT);

        // Sub IFD
        final PosDataInputStream reader = new PosDataInputStream(
                new PosByteArrayInputStream(this.tiff_ByteArray),
                super.tiff_StartPos);
        reader.skip(this.getExifOffset() + 2);
        if (this.ifd_number > 0) {
            for (int i = 0; i < this.ifd_number; i++) {
                this.ifd_sub[i] = IFDParse.parse(reader, byteOrder, startPosTiff, byteArrayTiff);
            }
        }
        // Next IFD
        super.next = IFDParse.readInt(reader, byteOrder);
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
                String.format("Exif Offset: %d", this.getExifOffset()));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));

        DefaultMutableTreeNode node;
        DefaultMutableTreeNode node_subifd;

        // Sub IFD
        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + (int) this.value[0],
                2 + this.ifd_number * IFD.SIZE,
                "Exif Sub IFD",
                ImageLoader.getShortcutIcon());
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD));
        node = new DefaultMutableTreeNode(comp);
        parentNode.add(node);

        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + (int) this.value[0],
                2,
                String.format("IFD number: %d", this.ifd_number));
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
                    String.format("Sub IFD[%d] - %s", i, this.ifd_sub[i].getTagName()));
            comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD));
            node_subifd = new DefaultMutableTreeNode(comp);
            node.add(node_subifd);
            if (this.ifd_sub[i] != null) {
                this.ifd_sub[i].generateTreeNode(node_subifd);
            }
        }

        // Next IFD
        super.generateTreeNode_NextIFD(node);
    }
    /**
     * @see IFD_9000_ExifVersion
     * @see IFD_A000_FlashpixVersion
     */
    public static final String FormatVersion = IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_FormatVersion);  // Section 4.2

    /**
     * @see IFD_9000_ExifVersion
     * @see IFD_A000_FlashpixVersion
     */
    static String getVersion(byte[] value) {
        final StringBuffer sb = new StringBuffer(5);
        sb.append((char) value[0]);  // byte A1
        sb.append((char) value[1]);  // byte A2
        sb.append('.');
        sb.append((char) value[2]);  // byte B1
        sb.append((char) value[3]);  // byte B2

        return sb.toString();
    }
}
