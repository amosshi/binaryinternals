/*
 * IFD_A002_PixelXDimension.java    Oct 28, 2010, 13:00
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
 * @see IFD_SHORT
 * @see IFD_LONG
 * @see IFD_8769_Exif#Category_C
 */
public class IFD_A002_PixelXDimension extends IFD_LONG_COUNT1 {

    public IFD_A002_PixelXDimension(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_LONG(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_A002_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_C));
    }
}
