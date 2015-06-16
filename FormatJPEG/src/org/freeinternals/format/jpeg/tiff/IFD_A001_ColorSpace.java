/*
 * IFD_A001_ColorSpace.java    Oct 28, 2010, 12:26
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
 * @see IFD_8769_Exif#Category_B
 */
public class IFD_A001_ColorSpace extends IFD_SHORT_COUNT1 {

    public IFD_A001_ColorSpace(final PosDataInputStream pDIS, int byteOrder, int tag, int startPosTiff, byte[] byteArrayTiff)
            throws IOException, FileFormatException {
        super(pDIS, byteOrder, tag, startPosTiff, byteArrayTiff);
    }

    @Override
    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        super.generateTreeNode_SHORT(
                parentNode,
                IFDMessage.getString(IFDMessage.KEY_IFD_A001_Description)
                + IFDMessage.getString(IFDMessage.KEY_IFD_8769_Exif_Category_B));
    }
}
