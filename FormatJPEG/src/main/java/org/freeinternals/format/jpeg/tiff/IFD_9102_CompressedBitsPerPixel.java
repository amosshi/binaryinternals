/*
 * IFD_9102_CompressedBitsPerPixel.java    Oct 30, 2010, 20:22
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.format.jpeg.tiff;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.format.FileFormatException;

/**
 *
 * @author Amos Shi
 * @see IFD_8769_Exif#Category_C
 */
public class IFD_9102_CompressedBitsPerPixel extends IFD_RATIONAL_COUNT1 {

    public IFD_9102_CompressedBitsPerPixel(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        this.generateTreeNode_RATIONAL(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_9102_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_C));
    }
}
