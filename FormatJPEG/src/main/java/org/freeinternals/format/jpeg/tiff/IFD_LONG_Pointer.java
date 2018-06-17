/*
 * IFD_LONG_Pointer.java    Oct 31, 2010, 11:55
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
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 * @see IFD_8769_Exif#Category_G
 */
public class IFD_LONG_Pointer extends IFD_LONG_COUNT1 {

    public final int ifd_number;
    public final IFD[] ifd_sub;
    int next;

    public IFD_LONG_Pointer(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);

        // Sub IFD

        final PosDataInputStream reader = new PosDataInputStream(
                new PosByteArrayInputStream(this.tiff_ByteArray),
                super.tiff_StartPos);
        reader.skip(super.value[0]);
        this.ifd_number = super.readUnsignedShort(reader);
        if (this.ifd_number > 0) {
            this.ifd_sub = new IFD[this.ifd_number];
        } else {
            this.ifd_sub = null;
        }
    }

    protected void generateTreeNode_NextIFD(DefaultMutableTreeNode parentNode) {
        if (this.ifd_sub != null) {
            parentNode.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                    this.ifd_sub[this.ifd_sub.length - 1].getStartPos() + IFD.SIZE,
                    4,
                    String.format("Next IFD: Offset within Tiff = %04X (%d)", this.next, this.next))));
        }
    }
}
