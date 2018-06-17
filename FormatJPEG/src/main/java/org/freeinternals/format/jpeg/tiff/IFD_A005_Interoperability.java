/*
 * IFD_A005_Interoperability.java    Oct 01, 2010, 10:17
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
public class IFD_A005_Interoperability extends IFD_LONG_Pointer {

    public static final String Category_A = IFDMessage.getString(IFDMessage.KEY_IFD_A005_Intero_Category_A);

    public IFD_A005_Interoperability(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        // Sub IFD
        final PosDataInputStream reader = new PosDataInputStream(
                new PosByteArrayInputStream(this.tiff_ByteArray),
                super.tiff_StartPos);
        reader.skip(this.getInteroperabilityOffset() + 2);
        if (this.ifd_number > 0) {
            for (int i = 0; i < this.ifd_number; i++) {
                this.ifd_sub[i] = IFDParse.parseIntero(reader, byteOrder, startPosTiff, byteArrayTiff);
                this.ifd_sub[i].setTagSpace(TagSpace.INTERO);
            }
        }
        // Next IFD
        super.next = IFDParse.readInt(reader, byteOrder);
    }

    public final long getInteroperabilityOffset() {
        return super.value[0];
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        int pos = super.startPos;
        super.generateTreeNode(parentNode, pos);

        JTreeNodeFileComponent comp;

        comp = new JTreeNodeFileComponent(
                pos + 8,
                4,
                String.format("Interoperability Offset: %d", super.value[0]));
        comp.setDescription(IFDMessage.getString(IFDMessage.KEY_IFD_Offset));
        parentNode.add(new DefaultMutableTreeNode(comp));

        DefaultMutableTreeNode node;
        DefaultMutableTreeNode node_subifd;

        // Sub IFD
        comp = new JTreeNodeFileComponent(
                super.tiff_StartPos + (int) this.value[0],
                2 + this.ifd_number * IFD.SIZE,
                "Interoperability Sub IFD",
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
}
